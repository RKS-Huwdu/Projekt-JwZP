package com.example.app.endpoints;

import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Get user by ID",
            description = "Get user from the database by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserById() {
        return userService.getCurrentUserInfo()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDto) {
        return userService.updateCurrentUser(userDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete user",
            description = "Delete user from the database by ID",
    responses = {
        @ApiResponse(
                responseCode = "200",
                description = "OK"
        )
    }
    )
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser() {
        return userService.deleteCurrentUser() ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordDTO passwordDTO) {
        return userService.updatePassword(passwordDTO) ?
                ResponseEntity.ok("Hasło zostało zmienione") :
                ResponseEntity.badRequest().build();
    }
}
