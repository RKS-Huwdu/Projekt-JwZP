package com.example.app.endpoints;

import com.example.app.entities.Category;
import com.example.app.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }


    @Operation(
            summary = "Pobierz wszystkie kategorie",
            description = "Pobiera listę wszystkich dostępnych kategorii.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Kategorie pobrane pomyślnie")
            }
    )
    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.findAll();
    }


    @Operation(
            summary = "Utwórz kategorię (tylko admin)",
            description = "Tworzy i zapisuje nową kategorię w systemie. Dostępne tylko dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Kategoria utworzona pomyślnie"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }


    @Operation(
            summary = "Usuń kategorię po ID (tylko admin)",
            description = "Trwale usuwa kategorię z systemu na podstawie jej ID. Dostępne tylko dla administratorów.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Kategoria usunięta pomyślnie (brak treści)"),
                    @ApiResponse(responseCode = "401", description = "Nieautoryzowany dostęp"),
                    @ApiResponse(responseCode = "403", description = "Brak uprawnień (wymagana rola ADMIN)"),
                    @ApiResponse(responseCode = "404", description = "Kategoria o podanym ID nie znaleziona")
            }
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoryByID(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
