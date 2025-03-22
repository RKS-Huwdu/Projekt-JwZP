package com.example.app.services;

import com.example.app.entities.Place;
import com.example.app.repositories.PlaceRepository;
import com.google.maps.errors.ApiException;
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

    public List<Place> findAll(){
        return placeRepository.findAll();
    }

    public Optional<Place> findById(Long id){
        return placeRepository.findById(id);
    }

    public Place save(Place place){
        return placeRepository.save(place);
    }

    public boolean deleteById(Long id){
        if(placeRepository.findById(id).isEmpty())
            return false;
        placeRepository.deleteById(id);
        return true;
    }

    public Place getNearestPlace() throws IOException, InterruptedException, ApiException {
        List<Place> places = placeRepository.findAll();

        PlacesSearchResponse foundNearBy = googleMapsService.getNearestPlaces();

        for(PlacesSearchResult result : foundNearBy.results){
            Optional<Place> nearestPlace = places.stream().filter(place -> place.getName().equalsIgnoreCase(result.name)).findFirst();
            if(nearestPlace.isPresent())
                return nearestPlace.get();
        }
        return null;
    }


}