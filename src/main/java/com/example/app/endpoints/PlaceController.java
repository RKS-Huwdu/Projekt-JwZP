package com.example.app.endpoints;

import com.example.app.entities.Place;
import com.example.app.services.PlaceService;
import com.google.maps.errors.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService){
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces(){
        return ResponseEntity.ok(placeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Place> getPlaceById(@PathVariable Long id){
        Optional<Place> place = placeService.findById(id);

        if(place.isPresent())
            return ResponseEntity.ok(place.get());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping
    public ResponseEntity<Place> createPlace(@RequestBody Place place) {
        return ResponseEntity.status(HttpStatus.CREATED).body(placeService.save(place));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        if(placeService.deleteById(id))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/nearest")
    public ResponseEntity<Place> getNearestPlace() {
        try {
            Place nearestPlace = placeService.getNearestPlace();
            return nearestPlace == null ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(nearestPlace);
        }catch(IOException | InterruptedException | ApiException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
