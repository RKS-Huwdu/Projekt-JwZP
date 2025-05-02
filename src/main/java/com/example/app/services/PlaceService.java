package com.example.app.services;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.dtos.UpdatePlaceDTO;
import com.example.app.entities.Category;
import com.example.app.entities.Place;
import com.example.app.entities.User;
import com.example.app.exception.CategoryNotFoundException;
import com.example.app.exception.PlaceNotFoundException;
import com.example.app.exception.ResourceOwnershipException;
import com.example.app.exception.UserNotFoundException;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaceService{
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    private final GoogleMapsService googleMapsService;

    private record ResolvedLocation(
            double latitude,
            double longitude,
            String address,
            String city,
            String country
    ) {}


    public PlaceService(PlaceRepository placeRepository,CategoryRepository categoryRepository,UserRepository userRepository,GoogleMapsService googleMapsService) {
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.googleMapsService = googleMapsService;
    }

    public List<PlaceDTO> findAll(String username){
        List<Place> places = placeRepository.findAllByUser_Username(username);

        return places.stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public PlaceDTO findById(String username,Long placeId){
        Place place = placeRepository.findByIdAndUser_Username(placeId, username)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found or does not belong to user"));
        return PlaceDTO.fromEntity(place);
    }

    @Transactional
    public PlaceDTO save(String username, CreatePlaceDTO dto){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        ResolvedLocation resolved = resolveLocation(dto.latitude(), dto.longitude(), dto.address());

        Category category = categoryRepository.findByName(dto.category())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Place place = new Place();
        place.setName(dto.name());
        place.setCategory(category);
        place.setLatitude(resolved.latitude());
        place.setLongitude(resolved.longitude());
        place.setAddress(resolved.address());
        place.setCity(resolved.city());
        place.setCountry(resolved.country());
        place.setNote(dto.note());
        place.setUser(user);

        user.getPlaces().add(place);
        placeRepository.save(place);

        return PlaceDTO.fromEntity(place);
    }

    @Transactional
    public void deleteById(String username, Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found with id: " + placeId));

        if (!place.getUser().getUsername().equals(username)) {
            throw new ResourceOwnershipException("User does not own this place");
        }

        placeRepository.delete(place);
    }

    @Transactional
    public PlaceDTO update(String username,Long placeId, UpdatePlaceDTO dto){
        Place place = placeRepository.findByIdAndUser_Username(placeId, username)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found or does not belong to user"));

        if((dto.latitude() != 0 && dto.longitude() != 0) || (dto.address() != null && !dto.address().isBlank())){
            ResolvedLocation resolved = resolveLocation(dto.latitude(), dto.longitude(), dto.address());
            place.setLatitude(resolved.latitude());
            place.setLongitude(resolved.longitude());
            place.setAddress(resolved.address());
            place.setCity(resolved.city());
            place.setCountry(resolved.country());
        }

        if (dto.category() != null && !dto.category().isBlank()) {
            Category category = categoryRepository.findByName(dto.category())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            place.setCategory(category);
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            place.setName(dto.name());
        }

        if (dto.note() != null && !dto.note().isBlank()) {
            place.setNote(dto.note());
        }

        placeRepository.save(place);

        return PlaceDTO.fromEntity(place);
    }

    @Transactional
    public PlaceDTO findNearestPlace(String username, double lat, double lng) {
        List<Place> places = placeRepository.findAllByUser_Username(username);
        if (places.isEmpty()) {
            throw new PlaceNotFoundException("User has no saved places");
        }

        Place nearest = places.stream()
                .min(Comparator.comparingDouble(p -> haversine(lat, lng, p.getLatitude(), p.getLongitude())))
                .orElseThrow();

        return PlaceDTO.fromEntity(nearest);
    }

    private ResolvedLocation resolveLocation(double lat, double lng, String address) {
        if ((lat == 0 || lng == 0) && address != null) {
            var geo = googleMapsService.geocodeAddress(address)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid address"));
            return new ResolvedLocation(geo.lat(), geo.lng(), geo.address(), geo.city(), geo.country());
        } else if ((address == null || address.isBlank()) && lat != 0 && lng != 0) {
            var geo = googleMapsService.reverseGeocode(lat, lng)
                    .orElseThrow(() -> new IllegalArgumentException("Could not resolve location"));
            return new ResolvedLocation(lat, lng, geo.address(), geo.city(), geo.country());
        } else {
            return new ResolvedLocation(lat, lng, address, "", "");
        }
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLon / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}