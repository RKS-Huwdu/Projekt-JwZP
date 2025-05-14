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


    @Operation(summary = "Get all categories", description = "Retrieve all categories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
            })
    @GetMapping
    public List<Category> getAllCategories(){
        return categoryService.findAll();
    }


    @Operation(summary = "Create a category", description = "Create and save a new category",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Category created successfully")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }


    @Operation(summary = "Delete a category", description = "Delete a category by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found")
            })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategoryByID(@PathVariable Long id) {
        categoryService.deleteById(id);
    }

//    @Operation(summary = "Delete a category", description = "Delete a category by its Name",
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
//                    @ApiResponse(responseCode = "404", description = "Category not found")
//            })
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{name}")
//    public void deleteCategoryByName(@PathVariable String name) {
//        categoryService.deleteByName(name);
//    }
}
