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
            summary = "Get all friends",
            description = "Retrieve a list of friends for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Friends list retrieved"),
                    @ApiResponse(responseCode = "404", description = "No friends found")
            }
    )
    @GetMapping("/friends")
    public List<FriendsDTO> getFriends(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getFriends(user.getUsername());
    }

    @Operation(
            summary = "Get all invitations",
            description = "Retrieve a list of invitations for the authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Invitations list retrieved"),
                    @ApiResponse(responseCode = "404", description = "No invitations found")
            }
    )
    @GetMapping("/invitations")
    public List<FriendsDTO> getInvitations(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getInvitations(user.getUsername());
    }

    @Operation(
            summary = "Invite a friend",
            description = "Send a friend invitation to a user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Friend invitation sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/{username}/invite-friend")
    public ResponseEntity<FriendsDTO> inviteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable String username) {
        return ResponseEntity.ok(friendService.sendInvitation(user.getUsername(), username));
    }

    @Operation(
            summary = "Delete a friend invitation",
            description = "Delete an invitation sent to a friend",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Invitation deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Invitation not found")
            }
    )
    @DeleteMapping("/{username}/invite-friend")
    public ResponseEntity<Void> deleteInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                 @PathVariable String username) {
        friendService.deleteInvitation(user.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Accept a friend invitation",
            description = "Accept a friend invitation from another user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Friend invitation accepted"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
            }
    )
    @PostMapping("/invitations/{username}/accept")
    public ResponseEntity<FriendsDTO> acceptInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                       @PathVariable String username) {
        return ResponseEntity.ok(friendService.acceptInvitation(user.getUsername(), username));
    }

    @Operation(
            summary = "Delete a friend",
            description = "Remove a friend from the authenticated user's friend list",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Friend deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Friend not found")
            }
    )
    @DeleteMapping("/{username}/delete-friend")
    public ResponseEntity<Void> deleteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable String username) {
        friendService.deleteFriend(user.getUsername(), username);  // Usuwamy znajomego
        return ResponseEntity.noContent().build();  // Zwracamy odpowiedź bez treści (status 204)
    }


}
