package com.example.app.services;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.dtos.UpdatePlaceDTO;
import com.example.app.entities.*;
import com.example.app.exception.*;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final FriendService friendService;
    private final GoogleMapsService googleMapsService;
    private final Clock clock;

    private static final int FREE_USER_PLACE_LIMIT = 10;

    private record ResolvedLocation(
            double latitude,
            double longitude,
            String address,
            String city,
            String country
    ) {
    }

    public PlaceService(PlaceRepository placeRepository, CategoryRepository categoryRepository, UserRepository userRepository, GoogleMapsService googleMapsService,FriendService friendService, Clock clock) {
        this.placeRepository = placeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.googleMapsService = googleMapsService;
        this.friendService = friendService;
        this.clock = clock;
    }

    public List<PlaceDTO> findAll(String username) {
        List<Place> places = placeRepository.findAllByUser_Username(username);

        return places.stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    public List<PlaceDTO> findAllPrivate(String username) {
        List<Place> places = placeRepository.findPrivatePlacesByUsername(username);

        return places.stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    public List<PlaceDTO> findFriendPlaces(String userUsername, String friendUsername) {
        if(!friendService.isFriendWith(userUsername, friendUsername)){
            throw new FriendNotFoundException("You have no friend named: " + friendUsername);
        }

        List<Place> places = placeRepository.findPublicPlacesByUsername(friendUsername);

        return places.stream()
                .map(PlaceDTO::fromEntity)
                .toList();
    }

    public PlaceDTO findById(String username, Long placeId) {
        Place place = placeRepository.findByIdAndUser_Username(placeId, username)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found or does not belong to user"));
        return PlaceDTO.fromEntity(place);
    }

    @Transactional
    public PlaceDTO save(String username, CreatePlaceDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        boolean isFreeUser = false;
        boolean isPremiumUser = false;
        boolean isAdmin = false;

        for (Role role : user.getRoles()) {
            if (role.getName() == RoleName.FREE_USER) {
                isFreeUser = true;
            } else if (role.getName() == RoleName.PREMIUM_USER) {
                isPremiumUser = true;
            } else if (role.getName() == RoleName.ADMIN) {
                isAdmin = true;
            }
        }

        if (isFreeUser && !isPremiumUser && !isAdmin) {
            int currentPlaceCount = user.getPlaces().size();
            if (currentPlaceCount >= FREE_USER_PLACE_LIMIT) {
                throw new PlaceLimitExceededException(
                        "Użytkownicy z darmowym planem mogą dodać maksymalnie " + FREE_USER_PLACE_LIMIT + " miejsc. " +
                                "Rozważ przejście na plan premium, aby dodawać miejsca bez limitu."
                );
            }
        }


        if(placeRepository.findByNameAndUser_Username(dto.name(),username).isPresent())
            throw new PlaceAlreadyExistsException("Place already exists: " + dto.name());

        Category category = categoryRepository.findByName(dto.category())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + dto.category()));

        ResolvedLocation resolved = resolveLocation(dto.latitude(), dto.longitude(), dto.address());

        boolean isPublic = dto.isPublic() == null || dto.isPublic();

        Place place = Place.builder()
                .name(dto.name())
                .category(category)
                .latitude(resolved.latitude())
                .longitude(resolved.longitude())
                .address(resolved.address())
                .city(resolved.city())
                .country(resolved.country())
                .note(dto.note())
                .user(user)
                .isPublic(isPublic)
                .postDate(OffsetDateTime.now(clock)).build();

        user.getPlaces().add(place);
        placeRepository.save(place);

        return PlaceDTO.fromEntity(place);
    }

    @Transactional
    public void deleteById(String username, Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found with id: " + placeId));

        if (!place.getUser().getUsername().equals(username)) {
            throw new ResourceOwnershipException("User does not own this place");
        }

        placeRepository.delete(place);
    }

    @Transactional
    public PlaceDTO update(String username, Long placeId, UpdatePlaceDTO dto) {
        Place place = placeRepository.findByIdAndUser_Username(placeId, username)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found or does not belong to user"));

        if (dto.category() != null && !dto.category().isBlank()) {
            Category category = categoryRepository.findByName(dto.category())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found" + dto.category()));
            place.setCategory(category);
        }

        if ((dto.latitude() != 0 && dto.longitude() != 0) || (dto.address() != null && !dto.address().isBlank())) {
            ResolvedLocation resolved = resolveLocation(dto.latitude(), dto.longitude(), dto.address());
            place.setLatitude(resolved.latitude());
            place.setLongitude(resolved.longitude());
            place.setAddress(resolved.address());
            place.setCity(resolved.city());
            place.setCountry(resolved.country());
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

    @Transactional
    public PlaceDTO findNearestPlace(String username, double lat, double lng,String category) {
        List<Place> places = placeRepository.findAllByCategoryAndUser_Username(category,username);
        if (places.isEmpty()) {
            throw new PlaceNotFoundException("User has no saved places of category: " + category);
        }

        Place nearest = places.stream()
                .min(Comparator.comparingDouble(p -> haversine(lat, lng, p.getLatitude(), p.getLongitude())))
                .orElseThrow();

        return PlaceDTO.fromEntity(nearest);
    }

    @Transactional
    public void share(String senderUsername,String receiverUsername, Long placeId){
        if (senderUsername.equals(receiverUsername)) {
            throw new CannotShareWithYourselfException("Cannot share place to yourself.");
        }
        Place place = placeRepository.findByIdAndUser_Username(placeId,senderUsername)
                .orElseThrow(() -> new PlaceNotFoundException("Place not found or does not belong to user"));

        User receiver = userRepository.findByUsername(receiverUsername)
                .orElseThrow(()-> new UserNotFoundException("User not found: " + receiverUsername));
        place.getSharedWith().add(receiver);
        receiver.getSharedPlaces().add(place);
        userRepository.save(receiver);
        placeRepository.save(place);
    }

    public List<PlaceDTO> findAllSharedPlaces(String username){
       return placeRepository.findAllSharedByUsername(username).stream()
                .map(PlaceDTO::fromEntity)
                .toList();
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