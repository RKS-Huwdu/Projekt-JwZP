package com.example.app.endpoints;

import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UpdateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.entities.PremiumStatus;
import com.example.app.entities.RoleName;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Pobierz dane bieżącego użytkownika",
            description = "Pobiera informacje o aktualnie zalogowanym użytkowniku.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dane użytkownika pobrane pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp (brak lub nieprawidłowy token)"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
            }
    )
    @GetMapping("/me")
    public UserDTO getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getCurrentUserInfo(user.getUsername());
    }

    @Operation(
            summary = "Pobierz wszystkich użytkowników",
            description = "Pobiera listę wszystkich użytkowników zarejestrowanych w systemie. Dostępne dla odpowiednio autoryzowanych użytkowników (np. Admin).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista użytkowników pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień do wykonania tej operacji")
            }
    )
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }


    @Operation(
            summary = "Sprawdź status premium bieżącego użytkownika",
            description = "Sprawdza, czy aktualnie zalogowany użytkownik posiada status konta premium.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status premium sprawdzony pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
            }
    )
    @GetMapping("/account/status")
    public PremiumStatus checkPremiumStatus(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getCurrentUserPremiumStatus(user.getUsername());
    }

    @Operation(
            summary = "Aktualizuj dane bieżącego użytkownika",
            description = "Aktualizuje wybrane dane (np. email, nazwa użytkownika) aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dane użytkownika zaktualizowane pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe (błędy walidacji)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony"),
                    @ApiResponse(responseCode = "409", description = "Konflikt danych (np. nowa nazwa użytkownika lub email jest już zajęty)")
            }
    )
    @PutMapping("/update")
    public UserDTO updateUser(@Valid @RequestBody UpdateUserDTO updateUserDto,
                              @AuthenticationPrincipal CustomUserDetails user) {
        return userService.updateCurrentUser(updateUserDto, user.getUsername());
    }

    @Operation(
            summary = "Usuń konto bieżącego użytkownika",
            description = "Trwale usuwa konto aktualnie zalogowanego użytkownika z systemu.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Konto użytkownika usunięte pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
            }
    )
    @DeleteMapping("/me")
    public void deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteCurrentUser(user.getUsername());
    }

    @Operation(
            summary = "Zmień hasło bieżącego użytkownika",
            description = "Aktualizuje hasło dla aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hasło zmienione pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane hasła (np. błędy walidacji)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony")
            }
    )
    @PatchMapping("/password")
    public String updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                 @AuthenticationPrincipal CustomUserDetails user) {
        userService.updatePassword(passwordDTO, user.getUsername());
        return "Hasło zostało zmienione";
    }

    @Operation(
            summary = "Pobierz użytkownika po ID (tylko admin)",
            description = "Pobiera szczegółowe informacje o użytkowniku na podstawie jego unikalnego ID. Operacja dostępna wyłącznie dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Dane użytkownika pobrane pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik o podanym ID nie znaleziony")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(
            summary = "Usuń użytkownika po ID (tylko admin)",
            description = "Trwale usuwa użytkownika z systemu na podstawie jego ID. Operacja dostępna wyłącznie dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Użytkownik usunięty pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik o podanym ID nie znaleziony")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @Operation(
            summary = "Dodaj rolę użytkownikowi (tylko admin)",
            description = "Przypisywanie określonej roli (np. PREMIUM_USER, ADMIN) użytkownikowi o zadanym ID. Operacja dostępna wyłącznie dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rola dodana użytkownikowi pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowa nazwa roli podana w ścieżce"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik lub rola (typ roli) nie znalezione")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role/{role}")
    public void addRoleToUser(@PathVariable Long id, @PathVariable RoleName role) {
        userService.addRoleToUser(id, role);
    }

    @Operation(
            summary = "Usuń rolę użytkownikowi (tylko admin)",
            description = "Odbieranie określonej roli użytkownikowi o zadanym ID. Operacja dostępna wyłącznie dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Rola usunięta użytkownikowi pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowa nazwa roli podana w ścieżce"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony lub użytkownik nie posiadał danej roli")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/role/{role}")
    public void deleteRoleFromUser(@PathVariable Long id, @PathVariable RoleName role) {
        userService.deleteRoleFromUser(id, role);
    }
}
