package com.example.app.endpoints;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.dtos.UpdatePlaceDTO;
import com.example.app.repositories.UserRepository;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService,UserRepository userRepository) {
        this.placeService = placeService;
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
    public List<PlaceDTO> getAllPlaces(@AuthenticationPrincipal CustomUserDetails user){
        return placeService.findAll(user.getUsername());
    }

    @Operation(
            summary = "Get all private places",
            description = "Retrieve a list of all private places for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Places list retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/private")
    public List<PlaceDTO> getAllPlacesPrivate(@AuthenticationPrincipal CustomUserDetails user){
        return placeService.findAllPrivate(user.getUsername());
    }

    @Operation(
            summary = "Get all private places",
            description = "Retrieve a list of all private places for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Places list retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/friend/{friendUsername}")
    public List<PlaceDTO> getAllFriendPlaces(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable String friendUsername){
        return placeService.findFriendPlaces(user.getUsername(), friendUsername);
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
    public PlaceDTO getPlaceById(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id){
        return placeService.findById(user.getUsername(),id);
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
    public ResponseEntity<PlaceDTO> savePlace(@AuthenticationPrincipal CustomUserDetails user,
                                              @Valid @RequestBody CreatePlaceDTO dto) {
        PlaceDTO place = placeService.save(user.getUsername(),dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(place);

    }

    @Operation(
            summary = "Delete a place",
            description = "Delete a place by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Place deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Place not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @DeleteMapping("/{id}")
    @Transactional
    public void deletePlace(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        placeService.deleteById(user.getUsername(),id);
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
    public PlaceDTO getNearestPlace(@AuthenticationPrincipal CustomUserDetails user,
                                                    @RequestParam double latitude,
                                                    @RequestParam double longitude) {
        return placeService.findNearestPlace(user.getUsername(), latitude, longitude);
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
    public PlaceDTO updatePlace(@AuthenticationPrincipal CustomUserDetails user,
                                                @PathVariable Long id,
                                                @Valid @RequestBody UpdatePlaceDTO dto) {

        return placeService.update(user.getUsername(), id, dto);
    }

    @Operation(
            summary = "Get all shared places",
            description = "Retrieve a list of all places shared with the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Places list retrieved"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/shared")
    public List<PlaceDTO> getSharedPlaces(@AuthenticationPrincipal CustomUserDetails user) {
        return placeService.findAllSharedPlaces(user.getUsername());
    }

    @PostMapping("/{id}/share/{receiverUsername}")
    @Transactional
    public ResponseEntity<PlaceDTO> share(@AuthenticationPrincipal CustomUserDetails user,
                                          @PathVariable Long id,
                                          @PathVariable String receiverUsername){
        PlaceDTO place = placeService.share(user.getUsername(),receiverUsername,id);
        return ResponseEntity.ok(place);
    }
}