package com.example.app.endpoints;

import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.Place;
import com.example.app.entities.User;
import com.example.app.repositories.UserRepository;
import com.example.app.services.PlaceService;
import com.google.maps.errors.ApiException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;
    private final UserRepository userRepository;

    public PlaceController(PlaceService placeService,UserRepository userRepository) {
        this.placeService = placeService;
            this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getAllPlaces(){
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(placeService.findAll(user.get()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDTO> getPlaceById(@PathVariable Long id){
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Optional<PlaceDTO> place = placeService.findById(user.get(),id);

        if(place.isPresent())
            return ResponseEntity.ok(place.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @PostMapping
    @Transactional
    public ResponseEntity<PlaceDTO> savePlace(@RequestBody PlaceDTO placeDTO) {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Optional<PlaceDTO> place = placeService.save(user.get(),placeDTO);
        if(place.isPresent())
            return ResponseEntity.status(HttpStatus.CREATED).body(place.get());

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if(placeService.deleteById(user.get(),id))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }


    @GetMapping("/nearest")
    public ResponseEntity<PlaceDTO> getNearestPlace() {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        try {
            Optional<Place> nearestPlace = placeService.getNearestPlace(user.get());
            return nearestPlace.isPresent() ? ResponseEntity.ok(PlaceDTO.fromEntity(nearestPlace.get())) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }catch(IOException | InterruptedException | ApiException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PlaceDTO> updatePlace(@PathVariable Long id,@RequestBody PlaceDTO placeDTO) {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Optional<PlaceDTO> placeOptional = placeService.update(user.get(), id, placeDTO);
        return placeOptional.isPresent() ? ResponseEntity.ok(placeOptional.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{id}/share/{username}")
    @Transactional
    public ResponseEntity<PlaceDTO> share(@PathVariable Long id,
                                          @PathVariable String username) throws Exception {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        PlaceDTO place = placeService.share(user.get(),username,id);
        return ResponseEntity.ok(place);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<PlaceDTO>> getSharedPlaces(){
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(placeService.findAllSharedPlaces(user.get()));
    }


    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

}