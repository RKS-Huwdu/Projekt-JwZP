package com.example.app.endpoints;

import com.example.app.dtos.UserDTO;
import com.example.app.entities.User;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;

    public PublicController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Register user",
            description = "Create user in the database",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created"
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody User user) {
        UserDTO savedUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

}
