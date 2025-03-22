package com.example.app.services;

import com.example.app.entities.Category;
import com.example.app.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return categoryRepository.save(category);
    }

    public boolean deleteById(Long id){
        if(categoryRepository.findById(id).isEmpty())
            return false;
        categoryRepository.deleteById(id);
        return true;
    }
}
