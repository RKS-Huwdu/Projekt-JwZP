package com.example.app.endpoints;

import com.example.app.dtos.CreateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.services.InfoService;
import com.example.app.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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
            summary = "Pobierz informacje o aplikacji",
            description = "Zwraca ogólne informacje o aplikacji.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Informacje o aplikacji pobrane pomyślnie")
            }
    )
    @GetMapping("/info")
    public String getAppInfo() {
        return infoService.getAppInfo();
    }


    @Operation(
            summary = "Rejestracja nowego użytkownika",
            description = "Umożliwia utworzenie nowego konta użytkownika w systemie.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Użytkownik zarejestrowany pomyślnie"),
                    @ApiResponse(responseCode = "400", description = "Nieprawidłowe dane wejściowe (błędy walidacji)"),
                    @ApiResponse(responseCode = "409", description = "Nazwa użytkownika lub email są już zajęte")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid CreateUserDTO user) {
        UserDTO savedUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

}
