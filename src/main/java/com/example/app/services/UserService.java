package com.example.app.services;

import com.example.app.DTOs.PasswordDTO;
import com.example.app.DTOs.UserDTO;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id).map(UserDTO::fromEntity);
    }

    public Optional<UserDTO> getCurrentUserInfo() {
        return getCurrentUser().map(UserDTO::fromEntity);
    }

    public UserDTO registerUser(User user) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(RoleName.FREE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found")));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roles);

        User newUser = userRepository.save(user);
        return UserDTO.fromEntity(newUser);
    }

    public boolean deleteById(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return true;
        }).orElse(false);
    }

    public boolean deleteCurrentUser() {
        return getCurrentUser()
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                }).orElse(false);
    }

    public Optional<UserDTO> updateCurrentUser(UserDTO userDto) {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return null;
        }

        User user = userOptional.get();
        if (userDto.username() != null) user.setUsername(userDto.username());
        if (userDto.email() != null) user.setEmail(userDto.email());

        user = userRepository.save(user);
        return Optional.of(UserDTO.fromEntity(user));
    }

    public boolean updatePassword(PasswordDTO passwordDTO) {
        Optional<User> userOptional = getCurrentUser();

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();

        user.setPassword(passwordEncoder.encode(passwordDTO.password()));
        userRepository.save(user);
        return true;
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }
}
