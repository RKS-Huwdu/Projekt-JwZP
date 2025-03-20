package com.example.app.components;

import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@Order(1)
public class RoleInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Arrays.stream(RoleName.values()).forEach(roleName -> {
            Optional<Role> existingRole = roleRepository.findByName(roleName);
            if (existingRole.isEmpty()) {
                Role role = new Role(roleName);
                roleRepository.save(role);
                System.out.println("Dodano rolę: " + roleName);
            }
        });
    }
}
