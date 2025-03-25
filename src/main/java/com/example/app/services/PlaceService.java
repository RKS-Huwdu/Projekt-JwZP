package com.example.app.services;

import com.example.app.entities.Place;
import com.example.app.entities.User;
import com.example.app.repositories.PlaceRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class PlaceService{
    private final PlaceRepository placeRepository;

    private final GoogleMapsService googleMapsService;

    public PlaceService(PlaceRepository placeRepository,GoogleMapsService googleMapsService) {
        this.placeRepository = placeRepository;
        this.googleMapsService = googleMapsService;
    }

    public List<Place> findAll(User user){
        return placeRepository.findAllByUserId(user.getId());
    }

    public Optional<Place> findById(User user,Long id){
        return placeRepository.findByUserIdAndPlaceId(user.getId(),id);
    }

    public Place save(User user,Place place){
        Optional<Place> placeOptional = placeRepository.findByName(place.getName());
        if(placeOptional.isPresent()){
            placeOptional.get().getUsers().add(user);
            return placeOptional.get();
        }
        
        return placeRepository.save(place);
    }


    public boolean deleteById(User user,Long id){
        Optional<Place> placeOptional = placeRepository.findById(id);
        if(placeOptional.isEmpty() || !placeOptional.get().getUsers().contains(user)){
            return false;
        }
        if(placeOptional.get().getUsers().size()==1)
            placeRepository.deleteById(id);
        else
            placeOptional.get().getUsers().remove(user);
        return true;
    }

    public Place getNearestPlace(User user) throws IOException, InterruptedException, ApiException {
        LatLng userLocation = googleMapsService.getUserLocation();
        double userLat = userLocation.lat;
        double userLng = userLocation.lng;

        List<Place> places = findAll(user);
        double minDistance = Double.MAX_VALUE;
        Place nearestPlace = null;

        for(Place place : places){
            double distance = getDistance(userLat,userLng,place.getLatitude(),place.getLongitude());
            if(distance < minDistance){
                minDistance = distance;
                nearestPlace = place;
            }
        }
        return nearestPlace;
    }

    private double getDistance(double userLat, double userLng, double placeLat, double placeLng){
        //Haversine formula
        return Math.abs(Math.asin(Math.sqrt(1 - Math.cos(placeLat - userLat) + Math.cos(userLat) * Math.cos(placeLat) * (1 - Math.cos(placeLng - userLng)))));
    }


}