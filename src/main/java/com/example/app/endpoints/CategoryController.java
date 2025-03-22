package com.example.app.endpoints;

import com.example.app.entities.Category;
import com.example.app.services.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService){
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.save(category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deletePlace(@PathVariable Long id) {
        if(categoryService.deleteById(id))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
