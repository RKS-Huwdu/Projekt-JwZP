package com.example.app.dtos;

import com.example.app.entities.Friends;
import com.example.app.entities.FriendshipStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendsDTO {
    private Long id;
    private String requesterUsername;
    private String receiverUsername;
    private FriendshipStatus status;
    private LocalDateTime createdAt;

    public static FriendsDTO fromEntity(Friends f) {
        return new FriendsDTO(
                f.getId(),
                f.getRequester().getUsername(),
                f.getReceiver().getUsername(),
                f.getStatus(),
                f.getCreatedAt()
        );
    }
}
