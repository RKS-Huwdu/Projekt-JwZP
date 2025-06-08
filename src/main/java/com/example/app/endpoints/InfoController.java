package com.example.app.endpoints;

import com.example.app.security.CustomUserDetails;
import com.example.app.services.InfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private final HealthEndpoint healthEndpoint;
    private final InfoService infoService;

    public InfoController(HealthEndpoint healthEndpoint, InfoService infoService) {
        this.healthEndpoint = healthEndpoint;
        this.infoService = infoService;
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
            summary = "Pobierz logi systemowe z danego dnia",
            description = "Zwraca zawartość pliku logów dla podanej daty w formacie RRRR-MM-DD, np. 2025-06-08. Dostęp tylko dla administratora.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Logi zostały pomyślnie pobrane"),
                    @ApiResponse(responseCode = "404", description = "Plik logów nie został znaleziony dla podanej daty"),
                    @ApiResponse(responseCode = "500", description = "Wewnętrzny błąd serwera")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/health")
    public HealthComponent getHealth(@AuthenticationPrincipal CustomUserDetails user){
        return healthEndpoint.health();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("logs/{date}")
    public String getLogByDate(@PathVariable String date) {
        return infoService.getLogs(date);
    }

}
