package com.example.app.services;

import com.example.app.dtos.FriendsDTO;
import com.example.app.entities.Friends;
import com.example.app.entities.FriendshipStatus;
import com.example.app.entities.User;
import com.example.app.exception.CannotInviteYourselfException;
import com.example.app.repositories.FriendsRepository;
import com.example.app.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FriendServiceTest {

    @Mock
    private FriendsRepository friendsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendService friendService;

    private User user1;
    private User user2;
    private Friends accepted;
    private Friends pending;


    @BeforeEach
    void setUp(){
        String username1 = "user1";
        String username2 = "user2";

        user1 = new User();
        user1.setUsername(username1);
        user1.setId(1L);

        user2 = new User();
        user2.setUsername(username2);
        user2.setId(2L);

        accepted = new Friends();
        accepted.setId(10L);
        accepted.setRequester(user1);
        accepted.setReceiver(user2);
        accepted.setStatus(FriendshipStatus.ACCEPTED);
        accepted.setCreatedAt(LocalDateTime.now());

        pending = new Friends();
        pending.setId(20L);
        pending.setRequester(user2);
        pending.setReceiver(user1);
        pending.setStatus(FriendshipStatus.PENDING);
        pending.setCreatedAt(LocalDateTime.now());
    }


    @Test
    void getFriendsTest() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByRequesterOrReceiver(user1, user1)).thenReturn(List.of(accepted, pending));

       List<FriendsDTO> result = friendService.getFriends(user1.getUsername());

       assertThat(result).hasSize(1);
       assertThat(result.get(0).getId()).isEqualTo(accepted.getId());
       assertThat(result.get(0).getRequesterUsername()).isEqualTo(user1.getUsername());
       assertThat(result.get(0).getReceiverUsername()).isEqualTo(user2.getUsername());
       assertThat(result.get(0).getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
    }


    @Test
    void getInvitationsTest(){
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByReceiverAndStatus(user1, FriendshipStatus.PENDING)).thenReturn(List.of(pending));

        List<FriendsDTO> result = friendService.getInvitations(user1.getUsername());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(pending.getId());
        assertThat(result.get(0).getRequesterUsername()).isEqualTo(user2.getUsername());
        assertThat(result.get(0).getReceiverUsername()).isEqualTo(user1.getUsername());
        assertThat(result.get(0).getStatus()).isEqualTo(FriendshipStatus.PENDING);
    }

    @Test
    void sendInvitationToYourselfTest(){
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByRequesterAndReceiver(user1, user1)).thenReturn(Optional.empty());

        assertThrows(CannotInviteYourselfException.class, () -> {
            friendService.sendInvitation(user1.getUsername(), user1.getUsername());
        });
    }

    @Test
    void sendInvitationTest(){
        Friends savedFriend = new Friends();
        savedFriend.setRequester(user1);
        savedFriend.setReceiver(user2);
        savedFriend.setStatus(FriendshipStatus.PENDING);

        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));
        when(friendsRepository.findByRequesterAndReceiver(user1, user2)).thenReturn(Optional.empty());
        when(friendsRepository.save(any(Friends.class))).thenReturn(savedFriend);

        FriendsDTO result = friendService.sendInvitation(user1.getUsername(), user2.getUsername());

        assertNotNull(result);
        assertEquals("user1", result.getRequesterUsername());
        assertEquals("user2", result.getReceiverUsername());
        assertEquals(FriendshipStatus.PENDING, result.getStatus());

        verify(friendsRepository).save(any(Friends.class));
    }

    @Test
    void acceptInvitation() {
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByRequesterAndReceiver(user2, user1)).thenReturn(Optional.of(pending));
        when(friendsRepository.save(pending)).thenReturn(pending);

        FriendsDTO result = friendService.acceptInvitation(user1.getUsername(), user2.getUsername());

        assertNotNull(result);
        assertEquals(user1.getUsername(), result.getReceiverUsername());
        assertEquals(user2.getUsername(), result.getRequesterUsername());
        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
    }

    @Test
    void acceptInvitation_userNotFound_throwsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            friendService.acceptInvitation("nonexistent", "nonexistent");
        });
    }


    @Test
    void rejectInvitation(){
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));
        when(friendsRepository.findByRequesterAndReceiver(user1, user2)).thenReturn(Optional.of(pending));

        friendService.deleteInvitation(user1.getUsername(), user2.getUsername());

        verify(friendsRepository).delete(pending);
        }

    @Test
    void deleteFriendship() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(userRepository.findByUsername(user2.getUsername())).thenReturn(Optional.of(user2));
        when(friendsRepository.findByRequesterAndReceiver(user1, user2)).thenReturn(Optional.of(accepted));

        friendService.deleteFriend(user1.getUsername(), user2.getUsername());

        verify(friendsRepository).delete(accepted);
    }

    @Test
    void deleteFriendship_userNotFound_throwsException(){
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            friendService.deleteFriend("nonexistent", "nonexistent");
        });
    }

    @Test
    void isFriendWith_whenUsersAreFriends_returnTrue() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByRequesterOrReceiver(user1, user1)).thenReturn(List.of(accepted));

        boolean result = friendService.isFriendWith(user1.getUsername(), user2.getUsername());

        assertTrue(result);
    }

    @Test
    void isFriendWith_whenUsersAreNotFriends_returnFalse() {
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(friendsRepository.findByRequesterOrReceiver(user1, user1)).thenReturn(List.of());

        boolean result = friendService.isFriendWith(user1.getUsername(), user2.getUsername());

        assertFalse(result);
    }

}
