package com.example.app.services;

import com.example.app.dtos.CreateUserDTO;
import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UpdateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.entities.PremiumStatus;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.exception.EmailAlreadyUsedException;
import com.example.app.exception.RoleNotFoundException;
import com.example.app.exception.UserNotFoundException;
import com.example.app.exception.UsernameAlreadyUsedException;
import com.example.app.repositories.RoleRepository;
import com.example.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_validData_returnsUserDTO() {
        CreateUserDTO dto = new CreateUserDTO("username", "email@email.com", "password");

        Role freeUserRole = new Role(RoleName.FREE_USER);

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("email@email.com")).thenReturn(false);
        when(roleRepository.findByName(RoleName.FREE_USER)).thenReturn(Optional.of(freeUserRole));
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("username");
        savedUser.setEmail("email@email.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(freeUserRole));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.registerUser(dto);

        assertThat(result.username()).isEqualTo(dto.username());
        assertThat(result.email()).isEqualTo(dto.email());
        assertThat(result.roles()).contains("FREE_USER");

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("password");
    }

    @Test
    void registerUser_emailAlreadyUsed_throwsException() {
        CreateUserDTO dto = new CreateUserDTO("username", "email@email.com", "password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());
        when(userRepository.existsByEmail("email@email.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(EmailAlreadyUsedException.class)
                .hasMessageContaining("Email");
    }

    @Test
    void registerUser_usernameAlreadyUsed_throwsException() {
        CreateUserDTO dto = new CreateUserDTO("username", "email@email.com", "password");

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(UsernameAlreadyUsedException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void updatePassword_validData_updatesPassword() {
        String username = "username";
        String rawPassword = "newPassword";
        String encodedPassword = "encodedPassword";

        PasswordDTO passwordDTO = new PasswordDTO(rawPassword);
        User user = new User();
        user.setUsername(username);
        user.setPassword("oldPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        userService.updatePassword(passwordDTO, username);

        assertThat(user.getPassword()).isEqualTo(encodedPassword);
        verify(userRepository).save(user);
    }

    @Test
    void updatePassword_userNotFound_throwsException() {
        String username = "username";
        PasswordDTO passwordDTO = new PasswordDTO("password");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(passwordDTO, username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void updateCurrentUser_validData_updatesAndReturnsDTO() {
        String originalUsername = "oldUser";
        String originalEmail = "old@example.com";
        String newUsername = "newUser";
        String newEmail = "new@example.com";

        User user = new User();
        user.setUsername(originalUsername);
        user.setEmail(originalEmail);

        UpdateUserDTO dto = new UpdateUserDTO(newUsername, newEmail);

        when(userRepository.findByUsername(originalUsername)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateCurrentUser(dto, originalUsername);

        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(result.username()).isEqualTo(newUsername);
        assertThat(result.email()).isEqualTo(newEmail);

        verify(userRepository).save(user);
    }

    @Test
    void updateCurrentUser_onlyEmail_updatesEmail() {
        String username = "user";
        String newEmail = "new@example.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail("old@example.com");

        UpdateUserDTO dto = new UpdateUserDTO(null, newEmail);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateCurrentUser(dto, username);

        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(result.email()).isEqualTo(newEmail);
    }

    @Test
    void updateCurrentUser_onlyUsername_updatesUsername() {
        String username = "user";
        String newUsername = "newUsername";
        String email = "email@example.com";

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);

        UpdateUserDTO dto = new UpdateUserDTO(newUsername, null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDTO result = userService.updateCurrentUser(dto, username);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(result.username()).isEqualTo(newUsername);
    }

    @Test
    void updateCurrentUser_userNotFound_throwsException() {
        String username = "unknown";
        UpdateUserDTO dto = new UpdateUserDTO("newUser", "new@example.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateCurrentUser(dto, username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getCurrentUserPremiumStatus_userHasPremiumRole_returnsPremium() {
        String username = "premiumUser";
        User user = new User();
        user.setUsername(username);

        Role premiumRole = new Role();
        premiumRole.setName(RoleName.PREMIUM_USER);
        user.setRoles(Set.of(premiumRole));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        PremiumStatus result = userService.getCurrentUserPremiumStatus(username);

        assertThat(result).isEqualTo(PremiumStatus.PREMIUM);
    }

    @Test
    void getCurrentUserPremiumStatus_userHasOnlyFreeRole_returnsNonPremium() {
        String username = "freeUser";
        User user = new User();
        user.setUsername(username);

        Role freeRole = new Role();
        freeRole.setName(RoleName.FREE_USER);
        user.setRoles(Set.of(freeRole));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        PremiumStatus result = userService.getCurrentUserPremiumStatus(username);

        assertThat(result).isEqualTo(PremiumStatus.NON_PREMIUM);
    }

    @Test
    void getCurrentUserPremiumStatus_userNotFound_throwsException() {
        String username = "unknown";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getCurrentUserPremiumStatus(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void addRoleToUser_validUserAndRole_addsRole() {
        Long userId = 1L;
        RoleName roleName = RoleName.PREMIUM_USER;

        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>());

        Role role = new Role();
        role.setName(roleName);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        userService.addRoleToUser(userId, roleName);

        assertThat(user.getRoles()).contains(role);
        verify(userRepository).save(user);
    }

    @Test
    void addRoleToUser_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addRoleToUser(1L, RoleName.PREMIUM_USER))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void addRoleToUser_roleNotFound_throwsException() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName(RoleName.PREMIUM_USER)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addRoleToUser(1L, RoleName.PREMIUM_USER))
                .isInstanceOf(RoleNotFoundException.class)
                .hasMessageContaining("Role not found");
    }

    @Test
    void deleteRoleFromUser_validUser_removesRole() {
        Long userId = 1L;
        RoleName roleName = RoleName.PREMIUM_USER;

        Role premiumRole = new Role();
        premiumRole.setName(roleName);

        Role freeRole = new Role();
        freeRole.setName(RoleName.FREE_USER);

        User user = new User();
        user.setId(userId);
        user.setRoles(new HashSet<>(Set.of(premiumRole, freeRole)));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteRoleFromUser(userId, roleName);

        assertThat(user.getRoles())
                .extracting(Role::getName)
                .doesNotContain(RoleName.PREMIUM_USER);

        verify(userRepository).save(user);
    }

    @Test
    void deleteRoleFromUser_userNotFound_throwsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteRoleFromUser(1L, RoleName.FREE_USER))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}
