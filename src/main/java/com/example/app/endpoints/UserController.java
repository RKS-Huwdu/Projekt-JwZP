package com.example.app.endpoints;

import com.example.app.DTOs.UserDTO;
import com.example.app.entities.User;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.findAll();
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
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Create user",
            description = "Create user in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created"
                    )
        }
    )
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteById(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
