package com.example.app.components;

import com.example.app.entities.Category;
import com.example.app.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoriesInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Restauracja",
            "Kawiarnia",
            "Sklep",
            "Park",
            "Muzeum",
            "Szkoła",
            "Kino",
            "Atrakcja turystyczna",
            "Plac zabaw",
            "Zabytek"
    );

    @Override
    public void run(String... args) {
        DEFAULT_CATEGORIES.forEach(this::createCategoryIfNotExists);
        logger.info("Dodano wstępne kategorie");
    }

    private void createCategoryIfNotExists(String name) {
        if (!categoryRepository.existsByName(name)) {
            Category category = new Category();
            category.setName(name);
            categoryRepository.save(category);
        }
    }
}