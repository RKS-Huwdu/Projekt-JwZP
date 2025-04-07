package com.example.app.endpoints;

import com.example.app.dtos.FriendsDTO;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.FriendService;
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

    @GetMapping("/friends")
    public List<FriendsDTO> getFriends(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getFriends(user.getUsername());
    }

    @GetMapping("/invitations")
    public List<FriendsDTO> getInvitations(@AuthenticationPrincipal CustomUserDetails user) {
        return friendService.getInvitations(user.getUsername());
    }

    @PostMapping("/{username}/invite-friend")
    public ResponseEntity<FriendsDTO> inviteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                                   @PathVariable String username) {
        return ResponseEntity.ok(friendService.sendInvitation(user.getUsername(), username));
    }

    @DeleteMapping("/{username}/invite-friend")
    public ResponseEntity<Void> deleteInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                 @PathVariable String username) {
        friendService.deleteInvitation(user.getUsername(), username);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/invitations/{username}/accept")
    public ResponseEntity<FriendsDTO> acceptInvitation(@AuthenticationPrincipal CustomUserDetails user,
                                                       @PathVariable String username) {
        return ResponseEntity.ok(friendService.acceptInvitation(user.getUsername(), username));
    }

    @DeleteMapping("/{username}/delete-friend")
    public ResponseEntity<Void> deleteFriend(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable String username) {
        friendService.deleteFriend(user.getUsername(), username);  // Usuwamy znajomego
        return ResponseEntity.noContent().build();  // Zwracamy odpowiedź bez treści (status 204)
    }


}
