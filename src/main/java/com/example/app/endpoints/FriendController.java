package com.example.app.endpoints;

import com.example.app.dtos.FriendsDTO;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @Operation(
            summary = "Pobierz wszystkich znajomych",
            description = "Pobiera listę wszystkich znajomych aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista znajomych pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Brak znajomych")
            }
    )
    @GetMapping("/friends")
    public List<FriendsDTO> getFriends(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getFriends(user.getUsername());
    }

    @Operation(
            summary = "Pobierz wszystkie zaproszenia do znajomych",
            description = "Pobiera listę oczekujących zaproszeń do znajomych dla aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista zaproszeń pobrana pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Brak zaproszeń")
            }
    )
    @GetMapping("/invitations")
    public List<FriendsDTO> getInvitations(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getInvitations(user.getUsername());
    }

    @Operation(
            summary = "Wyślij zaproszenie do znajomych",
            description = "Wysyła zaproszenie do znajomych do innego użytkownika. Użytkownik nie może wysłać zaproszenia do samego siebie ani do użytkownika, z którym już istnieje zaproszenie.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zaproszenie do znajomych wysłane pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe żądanie (np. próba zaproszenia samego siebie)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Użytkownik nie znaleziony"),
                    @ApiResponse(responseCode = "409", description = "Zaproszenie do tego użytkownika już istnieje")
            }
    )
    @PostMapping("/{username}/invite-friend")
    public ResponseEntity<FriendsDTO> inviteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable String username) {
        return ResponseEntity.ok(friendService.sendInvitation(user.getUsername(), username));
    }

    @Operation(
            summary = "Usuń wysłane zaproszenie do znajomych",
            description = "Usuwa zaproszenie do znajomych wysłane przez aktualnie zalogowanego użytkownika do innego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Zaproszenie usunięte pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Zaproszenie nie znalezione")
            }
    )
    @DeleteMapping("/{username}/invite-friend")
    public ResponseEntity<Void> deleteInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                 @PathVariable String username) {
        friendService.deleteInvitation(user.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Akceptuj zaproszenie do znajomych",
            description = "Akceptuje zaproszenie do znajomych od innego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zaproszenie do znajomych zaakceptowane pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe żądanie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Zaproszenie nie znalezione")
            }
    )
    @PostMapping("/invitations/{username}/accept")
    public ResponseEntity<FriendsDTO> acceptInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                       @PathVariable String username) {
        return ResponseEntity.ok(friendService.acceptInvitation(user.getUsername(), username));
    }

    @Operation(
            summary = "Usuń znajomego",
            description = "Usuwa znajomego z listy znajomych aktualnie zalogowanego użytkownika.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Znajomy usunięty pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "404", description = "Znajomy nie znaleziony")
            }
    )
    @DeleteMapping("/{username}/delete-friend")
    public ResponseEntity<Void> deleteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable String username) {
        friendService.deleteFriend(user.getUsername(), username);  // Usuwamy znajomego
        return ResponseEntity.noContent().build();  // Zwracamy odpowiedź bez treści (status 204)
    }


}
