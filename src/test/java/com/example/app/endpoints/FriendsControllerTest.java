package com.example.app.endpoints;


import com.example.app.dtos.FriendsDTO;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import com.example.app.exception.CannotInviteYourselfException;
import com.example.app.exception.FriendshipNotFoundException;
import com.example.app.exception.InvitationAlreadyExistsException;
import com.example.app.exception.InvitationNotFoundException;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class FriendsControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private FriendService friendService;

    private CustomUserDetails customUser;
    private final String testUsername = "testUser";
    private final String friendUsername = "friendUser";

    private FriendsDTO acceptedFriendDTO;
    private FriendsDTO pendingInvitationDTO;


    @BeforeEach
    void setUp(){
        Role role = new Role();
        role.setName(RoleName.FREE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User userEntity = new User();
        userEntity.setUsername(testUsername);
        userEntity.setPassword("password");
        userEntity.setId(1L);
        userEntity.setRoles(roles);
        userEntity.setEmail("test@example.com");

        customUser = new CustomUserDetails(userEntity);

        acceptedFriendDTO = new FriendsDTO();
        acceptedFriendDTO.setId(10L);
        acceptedFriendDTO.setRequesterUsername(testUsername);
        acceptedFriendDTO.setReceiverUsername(friendUsername);
        acceptedFriendDTO.setStatus(com.example.app.entities.FriendshipStatus.ACCEPTED);

        pendingInvitationDTO = new FriendsDTO();
        pendingInvitationDTO.setId(20L);
        pendingInvitationDTO.setRequesterUsername(friendUsername);
        pendingInvitationDTO.setReceiverUsername(testUsername);
        pendingInvitationDTO.setStatus(com.example.app.entities.FriendshipStatus.PENDING);

        when(friendService.getFriends(anyString())).thenReturn(Collections.emptyList());
        when(friendService.getInvitations(anyString())).thenReturn(Collections.emptyList());
        when(friendService.sendInvitation(anyString(), anyString())).thenReturn(null);
        when(friendService.acceptInvitation(anyString(),anyString())).thenReturn(null);
        doNothing().when(friendService).deleteInvitation(anyString(),anyString());
        doNothing().when(friendService).deleteFriend(anyString(),anyString());
    }
    @Test
    void shouldReturnForbiddenWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/user/friends"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnFriendsListWhenUserHasFriends() throws Exception {
        List<FriendsDTO> friendsList = Collections.singletonList(acceptedFriendDTO);
        when(friendService.getFriends(testUsername)).thenReturn(friendsList);

        mockMvc.perform(get("/user/friends").with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(acceptedFriendDTO.getId()))
                .andExpect(jsonPath("$[0].requesterUsername").value(acceptedFriendDTO.getRequesterUsername()))
                .andExpect(jsonPath("$[0].receiverUsername").value(acceptedFriendDTO.getReceiverUsername()))
                .andExpect(jsonPath("$[0].status").value(acceptedFriendDTO.getStatus().name()));


        verify(friendService, times(1)).getFriends(testUsername);
    }

    @Test
    void shouldReturnInvitationsListWhenUserHasInvitations() throws Exception {
        List<FriendsDTO> invitationsList = Collections.singletonList(pendingInvitationDTO);
        when(friendService.getInvitations(testUsername)).thenReturn(invitationsList);

        mockMvc.perform(get("/user/invitations").with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(pendingInvitationDTO.getId()))
                .andExpect(jsonPath("$[0].requesterUsername").value(pendingInvitationDTO.getRequesterUsername()))
                .andExpect(jsonPath("$[0].receiverUsername").value(pendingInvitationDTO.getReceiverUsername()))
                .andExpect(jsonPath("$[0].status").value(pendingInvitationDTO.getStatus().name()));

        verify(friendService, times(1)).getInvitations(testUsername);
    }

    @Test
    void shouldSendInvitationAndReturnDTO() throws Exception {
        FriendsDTO sentInviteResponse = new FriendsDTO();
        sentInviteResponse.setId(30L);
        sentInviteResponse.setRequesterUsername(testUsername);
        sentInviteResponse.setReceiverUsername(friendUsername);
        sentInviteResponse.setStatus(com.example.app.entities.FriendshipStatus.PENDING);

        when(friendService.sendInvitation(testUsername, friendUsername)).thenReturn(sentInviteResponse);

        mockMvc.perform(post("/user/{username}/invite-friend", friendUsername).with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sentInviteResponse.getId()))
                .andExpect(jsonPath("$.requesterUsername").value(sentInviteResponse.getRequesterUsername()))
                .andExpect(jsonPath("$.receiverUsername").value(sentInviteResponse.getReceiverUsername()))
                .andExpect(jsonPath("$.status").value(sentInviteResponse.getStatus().name()));

        verify(friendService, times(1)).sendInvitation(testUsername, friendUsername);
    }

    @Test
    void shouldAcceptInvitationAndReturnDTO() throws Exception {
        // Nadpisz domyślną konfigurację mocka dla tego konkretnego testu
        FriendsDTO acceptedInviteResponse = new FriendsDTO();
        acceptedInviteResponse.setId(pendingInvitationDTO.getId());
        acceptedInviteResponse.setRequesterUsername(friendUsername);
        acceptedInviteResponse.setReceiverUsername(testUsername);
        acceptedInviteResponse.setStatus(com.example.app.entities.FriendshipStatus.ACCEPTED);

        when(friendService.acceptInvitation(testUsername, friendUsername)).thenReturn(acceptedInviteResponse);

        mockMvc.perform(post("/user/invitations/{username}/accept", friendUsername).with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(acceptedInviteResponse.getId()))
                .andExpect(jsonPath("$.requesterUsername").value(acceptedInviteResponse.getRequesterUsername()))
                .andExpect(jsonPath("$.receiverUsername").value(acceptedInviteResponse.getReceiverUsername()))
                .andExpect(jsonPath("$.status").value(acceptedInviteResponse.getStatus().name()));


        verify(friendService, times(1)).acceptInvitation(testUsername, friendUsername);
    }

    @Test
    void shouldReturnBadRequestWhenInvitingSelf() throws Exception {
        when(friendService.sendInvitation(testUsername, testUsername))
                .thenThrow(new CannotInviteYourselfException("Cannot send invite to yourself."));

        mockMvc.perform(post("/user/{username}/invite-friend", testUsername).with(user(customUser)))
                .andExpect(status().isBadRequest()); // Oczekuj statusu 400 Bad Request

        verify(friendService, times(1)).sendInvitation(testUsername, testUsername);
    }

    @Test
    void shouldReturnBadRequestWhenInvitationAlreadyExists() throws Exception {
        when(friendService.sendInvitation(testUsername, friendUsername))
                .thenThrow(new InvitationAlreadyExistsException("Invitation already exists."));

        mockMvc.perform(post("/user/{username}/invite-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isConflict());
        verify(friendService, times(1)).sendInvitation(testUsername, friendUsername);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentInvitation() throws Exception {
        doThrow(new InvitationNotFoundException("Invitation not found")).when(friendService).deleteInvitation(testUsername, friendUsername);

        mockMvc.perform(delete("/user/{username}/invite-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isNotFound()); // Oczekuj statusu 404 Not Found

        verify(friendService, times(1)).deleteInvitation(testUsername, friendUsername);
    }

    @Test
    void shouldReturnNotFoundWhenAcceptingNonExistentInvitation() throws Exception {
        when(friendService.acceptInvitation(testUsername, friendUsername))
                .thenThrow(new InvitationNotFoundException("Invitation not found"));

        mockMvc.perform(post("/user/invitations/{username}/accept", friendUsername).with(user(customUser)))
                .andExpect(status().isNotFound()); // Oczekuj statusu 404 Not Found

        verify(friendService, times(1)).acceptInvitation(testUsername, friendUsername);
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentFriendship() throws Exception {
        doThrow(new FriendshipNotFoundException("Friendship not found")).when(friendService).deleteFriend(testUsername, friendUsername);

        mockMvc.perform(delete("/user/{username}/delete-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isNotFound()); // Oczekuj statusu 404 Not Found

        verify(friendService, times(1)).deleteFriend(testUsername, friendUsername);
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistForAnyEndpoint() throws Exception {
        when(friendService.getFriends(testUsername)).thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        mockMvc.perform(get("/user/friends").with(user(customUser)))
                .andExpect(status().isNotFound());

        verify(friendService, times(1)).getFriends(testUsername);
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToGetFriends() throws Exception {
        mockMvc.perform(get("/user/friends"))
                .andExpect(status().isUnauthorized()); // Oczekuj statusu 401 Unauthorized
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToGetInvitations() throws Exception {
        mockMvc.perform(get("/user/invitations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToInviteFriend() throws Exception {
        mockMvc.perform(post("/user/{username}/invite-friend", friendUsername))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToDeleteInvitation() throws Exception {
        mockMvc.perform(delete("/user/{username}/invite-friend", friendUsername))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToAcceptInvitation() throws Exception {
        mockMvc.perform(post("/user/invitations/{username}/accept", friendUsername))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticatedToDeleteFriend() throws Exception {
        mockMvc.perform(delete("/user/{username}/delete-friend", friendUsername))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnFriends() throws Exception{
        mockMvc.perform(get("/user/friends").with(user(customUser)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).getFriends(testUsername);
    }

    @Test
    void shouldReturnInvitations() throws Exception{
        mockMvc.perform(get("/user/invitations").with(user(customUser)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).getInvitations(testUsername);
    }

    @Test
    void shouldSendInvitation() throws Exception{
        mockMvc.perform(post("/user/{username}/invite-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).sendInvitation(testUsername, friendUsername);
    }


    @Test
    void shouldDeleteInvitation() throws Exception{
        mockMvc.perform(delete("/user/{username}/invite-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isNoContent());

        verify(friendService, times(1)).deleteInvitation(testUsername, friendUsername);
    }


    @Test
    void shouldAcceptInvitation() throws Exception{
        mockMvc.perform(post("/user/invitations/{username}/accept", friendUsername).with(user(customUser)))
                .andExpect(status().isOk());

        verify(friendService, times(1)).acceptInvitation(testUsername, friendUsername);
    }


    @Test
    void shouldDeleteFriend() throws Exception{
        mockMvc.perform(delete("/user/{username}/delete-friend", friendUsername).with(user(customUser)))
                .andExpect(status().isNoContent());

        verify(friendService, times(1)).deleteFriend(testUsername, friendUsername);
    }
}

