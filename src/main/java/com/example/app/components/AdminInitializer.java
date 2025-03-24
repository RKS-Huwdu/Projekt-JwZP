package com.example.app.components;

import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.app.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public AdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(RoleName.ADMIN)
                    .orElseThrow(() -> new RuntimeException("Default role not found")));

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setRoles(roles);
            userRepository.save(admin);
            logger.info("Admin user created successfully!");
        }
    }
}
