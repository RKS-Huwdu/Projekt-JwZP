package com.example.app.services;

import com.example.app.dtos.CreateUserDTO;
import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UpdateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.entities.PremiumStatus;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.exception.*;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
    }

    public UserDTO getCurrentUserInfo(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    public PremiumStatus getCurrentUserPremiumStatus(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        boolean isPremium = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.PREMIUM_USER);

        return isPremium ? PremiumStatus.PREMIUM : PremiumStatus.NON_PREMIUM;
    }

    public UserDTO registerUser(CreateUserDTO createUserDTO) {
        validateUniqueness(createUserDTO);

        Role defaultRole = roleRepository.findByName(RoleName.FREE_USER)
                .orElseThrow(() -> new RoleNotFoundException("Default role not found"));

        User newUser = toEntity(createUserDTO, defaultRole);
        User saved = userRepository.save(newUser);
        return UserDTO.fromEntity(saved);
    }

    public void deleteById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found"));
        userRepository.delete(user);
    }

    public void deleteCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        userRepository.delete(user);
    }

    public UserDTO updateCurrentUser(UpdateUserDTO updateUserDTO, String currentUsername) {
        User userToUpdate = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUsername));

        String newUsername = updateUserDTO.username();
        if (newUsername != null && !newUsername.isBlank() && !newUsername.equals(userToUpdate.getUsername())) {
            if (userRepository.findByUsername(newUsername).isPresent()) {
                throw new UsernameAlreadyUsedException("Nazwa użytkownika '" + newUsername + "' jest już zajęta.");
            }
            userToUpdate.setUsername(newUsername);
        }

        String newEmail = updateUserDTO.email();
        if (newEmail != null && !newEmail.isBlank()) {
            if (!newEmail.equals(userToUpdate.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {

                    throw new EmailAlreadyUsedException("Email '" + newEmail + "' jest już używany.");
                }
                userToUpdate.setEmail(newEmail);
            } else {
                userToUpdate.setEmail(newEmail);
            }
        }
        User updatedUser = userRepository.save(userToUpdate);
        return UserDTO.fromEntity(updatedUser);
    }

    public void updatePassword(PasswordDTO passwordDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));


        user.setPassword(passwordEncoder.encode(passwordDTO.password()));
        userRepository.save(user);
    }

    public void addRoleToUser(Long id, RoleName roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException("Role not found: " + roleName));

        user.getRoles().add(role);
        userRepository.save(user);
    }

    public void deleteRoleFromUser(Long id, RoleName roleName) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found: " + id));

        user.getRoles().removeIf(r -> r.getName().equals(roleName));

        userRepository.save(user);
    }

    private User toEntity(CreateUserDTO dto, Role defaultRole) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(Set.of(defaultRole));
        return user;
    }

    private void validateUniqueness(CreateUserDTO dto) {
        if (userRepository.findByUsername(dto.username()).isPresent()) {
            throw new UsernameAlreadyUsedException("Username already taken: " + dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyUsedException("Email already exists: " + dto.email());
        }
    }
}
