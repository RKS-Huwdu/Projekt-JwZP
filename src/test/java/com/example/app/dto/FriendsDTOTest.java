package com.example.app.dto;


import com.example.app.dtos.FriendsDTO;
import com.example.app.entities.Friends;
import com.example.app.entities.FriendshipStatus;
import com.example.app.entities.User;
import com.example.app.services.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class FriendsDTOTest {

    @Test
    void shouldMapFriendshipToDTO() {
        User user = new User();
        user.setId(1L);
        user.setUsername("john");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane");

        Friends friendship = new Friends();
        friendship.setId(1L);
        friendship.setRequester(user);
        friendship.setReceiver(user2);
        friendship.setStatus(FriendshipStatus.ACCEPTED);

        FriendsDTO dto = FriendsDTO.fromEntity(friendship);

        assertEquals(1L, dto.getId());
        assertEquals("john", dto.getRequesterUsername());
        assertEquals("jane", dto.getReceiverUsername());
        assertEquals(FriendshipStatus.ACCEPTED, dto.getStatus());
        assertTrue(dto.getCreatedAt().isBefore(LocalDateTime.now()));
}

}
