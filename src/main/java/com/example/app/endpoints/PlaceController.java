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
            summary = "Pobierz wszystkie miejsca użytkownika",
            description = "Pobiera listę wszystkich miejsc dodanych przez aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista miejsc pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping
    public List<PlaceDTO> getAllPlaces(@AuthenticationPrincipal CustomUserDetails user){
        return placeService.findAll(user.getUsername());
    }

    @Operation(
            summary = "Pobierz wszystkie prywatne miejsca użytkownika",
            description = "Pobiera listę wszystkich prywatnych miejsc dodanych przez aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista prywatnych miejsc pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping("/private")
    public List<PlaceDTO> getAllPlacesPrivate(@AuthenticationPrincipal CustomUserDetails user){
        return placeService.findAllPrivate(user.getUsername());
    }

    @Operation(
            summary = "Pobierz publiczne miejsca znajomego",
            description = "Pobiera listę publicznych miejsc dodanych przez wskazanego znajomego aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista miejsc znajomego pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Znajomy nie znaleziony")
            }
    )
    @GetMapping("/friend/{friendUsername}")
    public List<PlaceDTO> getAllFriendPlaces(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable String friendUsername){
        return placeService.findFriendPlaces(user.getUsername(), friendUsername);
    }

    @Operation(
            summary = "Pobierz miejsce po ID",
            description = "Pobiera szczegółowe informacje o miejscu na podstawie jego unikalnego ID, jeśli należy do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Miejsce pobrane pomyślnie"),
                    @ApiResponse(responseCode = "404", description = "Miejsce nie znalezione lub nie należy do użytkownika"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping("/{id}")
    public PlaceDTO getPlaceById(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id){
        return placeService.findById(user.getUsername(),id);
    }


    @Operation(
            summary = "Dodaj nowe miejsce",
            description = "Dodaje nowe miejsce dla aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Miejsce utworzone pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe (błędy walidacji)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "409", description = "Miejsce o podanej nazwie już istnieje dla użytkownika"),
                    @ApiResponse(responseCode = "403", description = "Limit miejsc dla darmowego konta został przekroczony")
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
            summary = "Usuń miejsce",
            description = "Trwale usuwa miejsce z systemu na podstawie jego ID, jeśli należy do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Miejsce usunięte pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "404", description = "Miejsce nie znalezione lub nie należy do użytkownika"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePlace(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        placeService.deleteById(user.getUsername(),id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Pobierz najbliższe miejsce",
            description = "Pobiera najbliższe miejsce do podanych współrzędnych geograficznych, należące do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Najbliższe miejsce znalezione"),
                    @ApiResponse(responseCode = "404", description = "Brak miejsc dla użytkownika"),
                    @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera (np. problem z geokodowaniem)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping("/nearest")
    public PlaceDTO getNearestPlace(@AuthenticationPrincipal CustomUserDetails user,
                                                    @RequestParam double latitude,
                                                    @RequestParam double longitude) {
        return placeService.findNearestPlace(user.getUsername(), latitude, longitude);
    }

    @Operation(
            summary = "Pobierz najbliższe miejsce w danej kategorii",
            description = "Pobiera najbliższe miejsce w określonej kategorii do podanych współrzędnych geograficznych, należące do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Najbliższe miejsce znalezione"),
                    @ApiResponse(responseCode = "404", description = "Brak miejsc w danej kategorii dla użytkownika"),
                    @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera (np. problem z geokodowaniem)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping("/nearest/{category}")
    public PlaceDTO getNearestPlace(@AuthenticationPrincipal CustomUserDetails user,
                                    @PathVariable String category,
                                    @RequestParam double latitude,
                                    @RequestParam double longitude) {
        return placeService.findNearestPlace(user.getUsername(), latitude, longitude,category);
    }

    @Operation(
            summary = "Zaktualizuj miejsce",
            description = "Aktualizuje szczegóły miejsca na podstawie jego ID, jeśli należy do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Miejsce zaktualizowane pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe (błędy walidacji)"),
                    @ApiResponse(responseCode = "404", description = "Miejsce nie znalezione lub nie należy do użytkownika"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
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
            summary = "Pobierz wszystkie miejsca udostępnione użytkownikowi",
            description = "Pobiera listę wszystkich miejsc, które zostały udostępnione aktualnie zalogowanemu użytkownikowi.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista udostępnionych miejsc pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp")
            }
    )
    @GetMapping("/shared")
    public List<PlaceDTO> getSharedPlaces(@AuthenticationPrincipal CustomUserDetails user) {
        return placeService.findAllSharedPlaces(user.getUsername());
    }

    @Operation(
            summary = "Udostępnij miejsce innemu użytkownikowi",
            description = "Udostępnia wybrane miejsce innemu użytkownikowi na podstawie jego nazwy użytkownika. Wymaga, aby udostępniane miejsce należało do aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Miejsce udostępnione pomyślnie"),
                    @ApiResponse(responseCode = "404", description = "Miejsce nie znalezione lub nie należy do użytkownika, albo użytkownik odbierający nie istnieje"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "400", description = "Nie można udostępnić miejsca, samemu sobie")
            }
    )
    @PostMapping("/{id}/share/{receiverUsername}")
    @Transactional
    public ResponseEntity<Void> share(@AuthenticationPrincipal CustomUserDetails user,
                                      @PathVariable Long id,
                                      @PathVariable String receiverUsername){
        placeService.share(user.getUsername(),receiverUsername,id);
        return ResponseEntity.ok().build();
    }
}