package com.example.app.endpoints;

import com.example.app.dtos.CreateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.services.InfoService;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final UserService userService;
    private final InfoService infoService;

    public PublicController(UserService userService, InfoService infoService) {
        this.userService = userService;
        this.infoService = new InfoService();
    }

    @Operation(
            summary = "Info",
            description = "Get info about application",
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/info")
    public String getAppInfo() {
        return infoService.getAppInfo();
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
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserDTO user) {
        UserDTO savedUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

}
