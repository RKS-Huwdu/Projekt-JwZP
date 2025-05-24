package com.example.app.services;

import com.example.app.entities.Category;
import com.example.app.exception.CategoryAlreadyExistsException;
import com.example.app.exception.CategoryNotFoundException;
import com.example.app.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll(){
        return categoryRepository.findAll();
    }

    public Category save(Category category){
        Optional<Category> existingCategory = categoryRepository.findByName(category.getName());
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Kategoria o nazwie '" + category.getName() + "' już istnieje.");
        }
        return categoryRepository.save(category);
    }

    public void deleteById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }

    public void deleteByName(String categoryName){
        Category category = categoryRepository.findByName(categoryName).orElseThrow(() -> new CategoryNotFoundException("Category not found: " + categoryName));
        categoryRepository.delete(category);
    }
}
