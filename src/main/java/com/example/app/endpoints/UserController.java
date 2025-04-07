package com.example.app.endpoints;

import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get current user",
            description = "Retrieve the currently authenticated user's information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getCurrentUserInfo(user.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update current user",
            description = "Update details of the currently authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDto,
                                              @AuthenticationPrincipal CustomUserDetails user) {
        return userService.updateCurrentUser(userDto, user.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete current user",
            description = "Delete the currently authenticated user from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.deleteCurrentUser(user.getUsername()) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
    @Operation(
            summary = "Change password",
            description = "Update the password of the currently authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password changed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid password data")
            }
    )
    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordDTO passwordDTO,
                                                 @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok("Hasło zostało zmienione");
    }

    @Operation(
            summary = "Get user by ID (admin only)",
            description = "Retrieve a user's information by ID (accessible only by admin)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    //admin endpoints
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete user by ID (admin only)",
            description = "Delete a user from the system using their ID (accessible only by admin)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        return userService.deleteById(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

}
