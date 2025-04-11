package com.example.app.endpoints;

import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.Place;
import com.example.app.entities.User;
import com.example.app.repositories.UserRepository;
import com.example.app.services.PlaceService;
import com.google.maps.errors.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(
            summary = "Get all places",
            description = "Retrieve a list of all places for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Places list retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping
    public ResponseEntity<List<PlaceDTO>> getAllPlaces(){
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        return ResponseEntity.ok(placeService.findAll(user.get()));
    }

    @Operation(
            summary = "Get place by ID",
            description = "Retrieve a place's information by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
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


    @Operation(
            summary = "Save a new place",
            description = "Save a new place for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Place created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid data provided"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
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

    @Operation(
            summary = "Delete a place",
            description = "Delete a place by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Place deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
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

    @Operation(
            summary = "Get the nearest place",
            description = "Retrieve the nearest place to the authenticated user's location",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nearest place found"),
                    @ApiResponse(responseCode = "404", description = "No places found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
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

    @Operation(
            summary = "Update a place",
            description = "Update place details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place updated successfully"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<PlaceDTO> updatePlace(@PathVariable Long id,@RequestBody PlaceDTO placeDTO) {
        Optional<User> user = getCurrentUser();
        if(user.isEmpty())
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Optional<PlaceDTO> placeOptional = placeService.update(user.get(), id, placeDTO);
        return placeOptional.isPresent() ? ResponseEntity.ok(placeOptional.get()) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

}