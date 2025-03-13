package com.example.app.services;

import com.example.app.entities.Place;
import com.example.app.repositories.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaceService{
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
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
}
