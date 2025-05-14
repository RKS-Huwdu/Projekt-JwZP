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
            summary = "Get current user",
            description = "Retrieve the currently authenticated user's information",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/me")
    public UserDTO getUser(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getCurrentUserInfo(user.getUsername());
    }

    @Operation(
            summary = "Get all users",
            description = "Get all users from the database",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    )
            }
    )
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
    }


    @Operation(
            summary = "Check premium status",
            description = "Check premium status of the currently authenticated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK")
            }
    )
    @GetMapping("/account/status")
    public PremiumStatus checkPremiumStatus(@AuthenticationPrincipal CustomUserDetails user) {
        return userService.getCurrentUserPremiumStatus(user.getUsername());
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
    public UserDTO updateUser(@Valid @RequestBody UpdateUserDTO updateUserDto,
                              @AuthenticationPrincipal CustomUserDetails user) {
        return userService.updateCurrentUser(updateUserDto, user.getUsername());
    }

    @Operation(
            summary = "Delete current user",
            description = "Delete the currently authenticated user from the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @DeleteMapping("/me")
    public void deleteUser(@AuthenticationPrincipal CustomUserDetails user) {
        userService.deleteCurrentUser(user.getUsername());
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
    public String updatePassword(@Valid @RequestBody PasswordDTO passwordDTO,
                                 @AuthenticationPrincipal CustomUserDetails user) {
        userService.updatePassword(passwordDTO, user.getUsername());
        return "Hasło zostało zmienione";
    }

    @Operation(
            summary = "Get user by ID (admin only)",
            description = "Retrieve a user's information by ID (accessible only by admin)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User data retrieved"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @Operation(
            summary = "Delete user by ID (admin only)",
            description = "Delete a user from the system using their ID (accessible only by admin)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
    }

    @Operation(
            summary = "Add user role by ID (admin only)",
            description = "Add user role by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role added successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role/{role}")
    public void addRoleToUser(@PathVariable Long id, @PathVariable RoleName role) {
        userService.addRoleToUser(id, role);
    }

    @Operation(
            summary = "Delete user role by ID (admin only)",
            description = "Delete user role by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/role/{role}")
    public void deleteRoleFromUser(@PathVariable Long id, @PathVariable RoleName role) {
        userService.deleteRoleFromUser(id, role);
    }
}
