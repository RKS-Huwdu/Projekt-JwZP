package com.example.app.endpoints;


import com.example.app.dtos.PasswordDTO;
import com.example.app.dtos.UpdateUserDTO;
import com.example.app.dtos.UserDTO;
import com.example.app.entities.PremiumStatus;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.exception.UserNotFoundException;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @MockitoBean
    private UserService userService; 

    private CustomUserDetails customUserFree;
    private CustomUserDetails customUserPremium;
    private UserDTO userFreeDTO; 
    private UserDTO userPremiumDTO;
    private List<UserDTO> allUsersDTOList; 
    
    private final String freeUsername = "freeUser";
    private final String freePassword = "freePassword";
    private final String freeEmail = "free@example.com";
    private final Long freeUserId = 1L;

    private final String premiumUsername = "premiumUser";
    private final String premiumPassword = "premiumPassword2"; 
    private final String premiumEmail = "premium@example.com";
    private final Long premiumUserId = 2L; 

    @BeforeEach
    public void setUp(){
        Role freeRole = new Role();
        freeRole.setName(RoleName.FREE_USER);

        Role premiumRole = new Role();
        premiumRole.setName(RoleName.PREMIUM_USER);

        User userFree = new User();
        userFree.setId(freeUserId);
        userFree.setUsername(freeUsername);
        userFree.setPassword(freePassword);
        userFree.setEmail(freeEmail);
        Set<Role> freeUserRoles = new HashSet<>();
        freeUserRoles.add(freeRole);
        userFree.setRoles(freeUserRoles);

        userFreeDTO = UserDTO.fromEntity(userFree);
        customUserFree = new CustomUserDetails(userFree);

        User userPremium = new User();
        userPremium.setId(premiumUserId);
        userPremium.setUsername(premiumUsername);
        userPremium.setPassword(premiumPassword);
        userPremium.setEmail(premiumEmail);
        Set<Role> premiumUserRoles = new HashSet<>();
        premiumUserRoles.add(freeRole);
        premiumUserRoles.add(premiumRole);
        userPremium.setRoles(premiumUserRoles);

        userPremiumDTO = UserDTO.fromEntity(userPremium);
        customUserPremium = new CustomUserDetails(userPremium);

        allUsersDTOList = List.of(userFreeDTO, userPremiumDTO);

        when(userService.getCurrentUserInfo(freeUsername)).thenReturn(userFreeDTO);
        when(userService.findById(freeUserId)).thenReturn(userFreeDTO);
        when(userService.getCurrentUserPremiumStatus(freeUsername)).thenReturn(userFree.getRoles().stream().anyMatch(role -> role.getName() == RoleName.PREMIUM_USER) ? PremiumStatus.PREMIUM : PremiumStatus.NON_PREMIUM);

        when(userService.getCurrentUserInfo(premiumUsername)).thenReturn(userPremiumDTO);
        when(userService.findById(premiumUserId)).thenReturn(userPremiumDTO);
        when(userService.getCurrentUserPremiumStatus(premiumUsername)).thenReturn(userPremium.getRoles().stream().anyMatch(role -> role.getName() == RoleName.PREMIUM_USER) ? PremiumStatus.PREMIUM : PremiumStatus.NON_PREMIUM);

        when(userService.findAll()).thenReturn(allUsersDTOList);
    }

    @Test
    void unauthorizedAccess_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/user/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void noAdminRole_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user/{id}",2L).with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserInfo_shouldReturnFreeUserDTO() throws Exception {
        mockMvc.perform(get("/user/me").with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(freeUserId))
                .andExpect(jsonPath("$.username").value(freeUsername))
                .andExpect(jsonPath("$.email").value(freeEmail))
                .andExpect(jsonPath("$.roles[0]").value(RoleName.FREE_USER.name()))
                .andExpect(jsonPath("$.places").isEmpty());
    }

    @Test
    void getUserPremiumStatus_shouldReturnPremium() throws Exception {
        when(userService.getCurrentUserPremiumStatus(premiumUsername)).thenReturn(PremiumStatus.PREMIUM);

        mockMvc.perform(get("/user/account/status").with(user(customUserPremium))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("\"PREMIUM\""));
    }

    @Test
    void updateUserWithNotValidData_shouldReturnBadRequest() throws Exception {
        String invalidUserJson = "{ \"username\": \"\", \"email\": \"invalidEmail\" }";

        mockMvc.perform(put("/user/update")
                        .with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUserWithValidData_shouldReturnUpdatedUser() throws Exception {
        UserDTO returnedUserDto = new UserDTO(null,"updatedUser", "updated@example.com",null,null);

        when(userService.updateCurrentUser(any(UpdateUserDTO.class), eq(freeUsername)))
                .thenReturn(returnedUserDto);


        String validUserJson = "{ \"username\": \"updatedUser\", \"email\": \"updated@example.com\" }";

        mockMvc.perform(put("/user/update")
                        .with(user(customUserFree))
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void updateUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        String validUserJson = "{ \"username\": \"updatedUser\", \"email\":  \"updated@example.com\"}";

        User nonExistentUser = new User();
        nonExistentUser.setUsername("nonExistentUser");
        CustomUserDetails customUserNonExistent = new CustomUserDetails(nonExistentUser);

        when(userService.updateCurrentUser(any(UpdateUserDTO.class), eq("nonExistentUser")))
                .thenThrow(new UserNotFoundException("User not found: "+ nonExistentUser.getUsername()));

        mockMvc.perform(put("/user/update")
                        .with(user(customUserNonExistent))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/user/me")
                        .with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updatePassword_shouldReturnOkOnSuccess() throws Exception {
        String validPasswordJson = "{ \"password\": \"newPassword123\" }";

        mockMvc.perform(patch("/user/password")
                        .with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPasswordJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Hasło zostało zmienione"));
    }

    @Test
    void updatePassword_shouldReturnBadRequestForInvalidData() throws Exception {
        String invalidPasswordJson = "{ \"password\": \"\" }";

        mockMvc.perform(patch("/user/password")
                        .with(user(customUserFree))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPasswordJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserById_shouldReturnUserDTOAsAdmin() throws Exception {
        when(userService.findById(freeUserId)).thenReturn(userFreeDTO);

        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        mockMvc.perform(get("/user/{id}", freeUserId)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(freeUserId))
                .andExpect(jsonPath("$.username").value(freeUsername))
                .andExpect(jsonPath("$.email").value(freeEmail))
                .andExpect(jsonPath("$.roles[0]").value(RoleName.FREE_USER.name()))
                .andExpect(jsonPath("$.places").isEmpty());
    }

    @Test
    void deleteUserById_shouldReturnNoContentAsAdmin() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        mockMvc.perform(delete("/user/{id}", freeUserId)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addRoleToUser_shouldReturnNoContentAsAdmin() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        mockMvc.perform(patch("/user/{id}/role/{role}", freeUserId, RoleName.PREMIUM_USER)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addRoleToUser_shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        doThrow(new UserNotFoundException("User with ID " + freeUserId + " not found"))
                .when(userService).addRoleToUser(eq(freeUserId), eq(RoleName.PREMIUM_USER));

        mockMvc.perform(patch("/user/{id}/role/{role}", freeUserId, RoleName.PREMIUM_USER)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserById_shouldReturnNotFoundWhenUserDoesNotExistAsAdmin() throws Exception {
        when(userService.findById(freeUserId)).thenThrow(new UserNotFoundException("User with ID " + freeUserId + " not found"));
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        doThrow(new UserNotFoundException("User with ID " + freeUserId + " not found"))
                .when(userService).deleteById(freeUserId);

        mockMvc.perform(delete("/user/{id}", freeUserId)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addRoleToUser_shouldReturnBadRequestForInvalidRole() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        mockMvc.perform(patch("/user/{id}/role/{role}", freeUserId, "INVALID_ROLE")
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteRoleFromUser_shouldReturnNoContentAsAdmin() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        mockMvc.perform(patch("/user/{id}/role/{role}", freeUserId, RoleName.FREE_USER)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRoleFromUser_shouldReturnNotFoundWhenRoleNotPresent() throws Exception{
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setUsername("adminUser");
        adminUser.setRoles(Set.of(new Role(RoleName.ADMIN)));
        CustomUserDetails customUserAdmin = new CustomUserDetails(adminUser);

        doThrow(new UserNotFoundException("User with ID " + freeUserId + " not found"))
                .when(userService).deleteRoleFromUser(eq(freeUserId), eq(RoleName.PREMIUM_USER));

        mockMvc.perform(delete("/user/{id}/role/{role}", freeUserId, RoleName.PREMIUM_USER)
                        .with(user(customUserAdmin))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
