package com.example.app.endpoints;

import com.example.app.entities.Category;
import com.example.app.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }


    @Operation(summary = "Get all categories", description = "Retrieve all categories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
            })
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok(categoryService.findAll());
    }


    @Operation(summary = "Create a category", description = "Create and save a new category",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully")
            })
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }


    @Operation(summary = "Delete a category", description = "Delete a category by its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id) {
        if(categoryService.deleteById(id))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
