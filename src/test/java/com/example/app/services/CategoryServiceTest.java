package com.example.app.services;


import com.example.app.entities.Category;
import com.example.app.exception.CategoryNotFoundException;
import com.example.app.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    private Category category1;
    private Category category2;


    @BeforeEach
    public void setUp() {
        categoryService = new CategoryService(categoryRepository);

        category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");

        category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");

        when(categoryRepository.findByName("Category1")).thenReturn(java.util.Optional.of(category1));
        when(categoryRepository.findByName("Category2")).thenReturn(java.util.Optional.of(category2));
        when(categoryRepository.existsByName("Category1")).thenReturn(true);
        when(categoryRepository.existsByName("Category2")).thenReturn(true);
        when(categoryRepository.existsByName("Category3")).thenReturn(false);
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));
    }


    @Test
    public void testFindAll() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<Category> categories = categoryService.findAll();

        assertThat(categories).isNotNull();
        assertThat(categories.size()).isEqualTo(2);
        verify(categoryRepository).findAll();
    }

    @Test
    public void testSave() {
        Category newCategory = new Category();
        newCategory.setName("newCategory");

        when(categoryRepository.save(newCategory)).thenReturn(newCategory);

        Category saved = categoryService.save(newCategory);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("newCategory");
        verify(categoryRepository).save(newCategory);
    }

    @Test
    public void testDeletionById(){
        Long idToDelete = 1L;

        when(categoryRepository.findById(idToDelete)).thenReturn(java.util.Optional.of(category1));

        categoryService.deleteById(1L);

        verify(categoryRepository).delete(category1);
    }

    @Test
    public void testDeleteByIdNotFound() {
        when(categoryRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteById(999L);
        });
    }

    @Test
    public void testDeleteByNameExists() {
        when(categoryRepository.findByName("Category1")).thenReturn(java.util.Optional.of(category1));

        categoryService.deleteByName("Category1");

        verify(categoryRepository).delete(category1);
    }

    @Test
    public void testDeleteByNameNotFound() {
        when(categoryRepository.findByName("NonExisting")).thenReturn(java.util.Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(CategoryNotFoundException.class, () -> {
            categoryService.deleteByName("NonExisting");
        });
    }

}