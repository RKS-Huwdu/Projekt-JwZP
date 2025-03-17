package com.example.app.services;

import com.example.app.DTOs.UserDTO;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(UserDTO::fromEntity);
    }

    public User save(User user) {
        setDefaultRole(user);
        return userRepository.save(user);
    }

    private void setDefaultRole(User user) {
        Set<Role> roles = new HashSet<>();
        Role defaultRole = roleRepository.findByName(RoleName.FREE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        roles.add(defaultRole);
        user.setRoles(roles);
    }

    public boolean deleteById(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }
}
