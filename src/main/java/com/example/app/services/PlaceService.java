package com.example.app.services;

import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.Category;
import com.example.app.entities.Place;
import com.example.app.entities.User;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaceService{
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;
    private final GoogleMapsService googleMapsService;


    public PlaceService(PlaceRepository placeRepository,CategoryRepository categoryRepository,UserRepository userRepository,GoogleMapsService googleMapsService) {
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.googleMapsService = googleMapsService;
    }

    public List<PlaceDTO> findAll(User user){
        return placeRepository.findAllByUserId(user.getId()).stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<PlaceDTO> findById(User user,Long id){
        return placeRepository.findByUserIdAndPlaceId(user.getId(),id).map(PlaceDTO::fromEntity);
    }

    @Transactional
    public Optional<PlaceDTO> save(User user,PlaceDTO placeDTO){
        Optional<Place> placeOptional = placeRepository.findByName(placeDTO.name());
        Place place;
        if(placeOptional.isPresent())
           place = placeOptional.get();
        else {
            Optional<Category> category = categoryRepository.findByName(placeDTO.category());
            if (category.isEmpty())
                return Optional.empty();

            place = new Place(placeDTO, category.get());
        }

        place.getUsers().add(user);
        user.getPlaces().add(place);
        userRepository.saveAndFlush(user);
        return Optional.of(PlaceDTO.fromEntity(placeRepository.saveAndFlush(place)));
    }

    @Transactional
    public boolean deleteById(User user,Long id){
        Optional<Place> placeOptional = placeRepository.findByUserIdAndPlaceId(user.getId(),id);
        if(placeOptional.isEmpty()){
            return false;
        }

        Place place = placeOptional.get();

        place.getUsers().remove(user);
        if (place.getUsers().isEmpty())
            placeRepository.delete(place);
        else
            placeRepository.saveAndFlush(place);
        return true;
    }

    @Transactional
    public Optional<PlaceDTO> update(User user,Long placeId,PlaceDTO placeDTO){
        Optional<Place> placeOptional = placeRepository.findByUserIdAndPlaceId(user.getId(),placeId);

        if(placeOptional.isEmpty())
            return Optional.empty();

        Place place = placeOptional.get();
        place.setName(placeDTO.name());
        place.setLatitude(placeDTO.latitude());
        place.setLongitude(placeDTO.longitude());

        Optional<Category> category = categoryRepository.findByName(placeDTO.category());
        if(placeDTO.category() != null && category.isPresent())
            place.setCategory(category.get());
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Category not found");

        placeRepository.saveAndFlush(place);
        return placeOptional.map(PlaceDTO::fromEntity);
    }

    public Optional<Place> getNearestPlace(User user) throws IOException, InterruptedException, ApiException {
        LatLng userLocation = googleMapsService.getUserLocation();
        double userLat = userLocation.lat;
        double userLng = userLocation.lng;

        List<Place> places = placeRepository.findAllByUserId(user.getId());

        if(places.isEmpty())
            return Optional.empty();

        double minDistance = Double.MAX_VALUE;
        Place nearestPlace = null;

        for(Place place : places){
            double distance = getDistance(userLat,userLng,place.getLatitude(),place.getLongitude());
            if(distance < minDistance){
                minDistance = distance;
                nearestPlace = place;
            }
        }
        return Optional.of(nearestPlace);
    }

    private double getDistance(double userLat, double userLng, double placeLat, double placeLng){
        return Math.abs(Math.asin(Math.sqrt(1 - Math.cos(placeLat - userLat) + Math.cos(userLat) * Math.cos(placeLat) * (1 - Math.cos(placeLng - userLng)))));
    }

    @Transactional
    public PlaceDTO share(User user,String receiverUsername, Long placeId) throws Exception {
        Place place = placeRepository.findByUsernameAndPlaceId(user.getUsername(),placeId)
                .orElseThrow(() -> new Exception("Place not found or does not belong to user"));

        //User sender = userRepository.findByUsername(user.getUsername())
               // .orElseThrow(()-> new Exception("User not found: " + user.getUsername()));

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(()-> new Exception("User not found: " + receiverUsername));
        place.getUsersShared().add(receiver);
        receiver.getSharedPlaces().add(place);
        userRepository.saveAndFlush(receiver);
        return PlaceDTO.fromEntity(placeRepository.saveAndFlush(place));
    }

    @Transactional
    public List<PlaceDTO> findAllSharedPlaces(User user){
        //User user = userRepository.findByUsername(user.getUsername())
               // .orElseThrow(()-> new Exception("User not found: " + username));

        return placeRepository.findAllSharedByUserId(user.getId()).stream()
                .map(PlaceDTO::fromEntity)
                .collect(Collectors.toList());
    }

}