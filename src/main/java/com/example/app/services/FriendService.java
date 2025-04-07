package com.example.app.services;

import com.example.app.dtos.FriendsDTO;
import com.example.app.entities.Friends;
import com.example.app.entities.FriendshipStatus;
import com.example.app.entities.User;
import com.example.app.repositories.FriendsRepository;
import com.example.app.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendService {

    private final FriendsRepository friendRepository;
    private final UserRepository userRepository;

    public FriendService(FriendsRepository friendRepository, UserRepository userRepository) {
        this.friendRepository = friendRepository;
        this.userRepository = userRepository;
    }

    public List<FriendsDTO> getFriends(String username) {
        User user = getUser(username);
        return friendRepository.findByRequesterOrReceiver(user, user).stream()
                .filter(friend -> friend.getStatus() == FriendshipStatus.ACCEPTED)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FriendsDTO> getInvitations(String username) {
        User user = getUser(username);
        return friendRepository.findByReceiverAndStatus(user, FriendshipStatus.PENDING).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FriendsDTO sendInvitation(String requesterUsername, String receiverUsername) {
        User requester = getUser(requesterUsername);
        User receiver = getUser(receiverUsername);

        if (requester.equals(receiver)) {
            throw new IllegalArgumentException("Cannot send invite to yourself.");
        }

        friendRepository.findByRequesterAndReceiver(requester, receiver)
                .ifPresent(f -> {
                    throw new IllegalStateException("Invitation already exists.");
                });

        Friends friends = new Friends();
        friends.setRequester(requester);
        friends.setReceiver(receiver);
        friends.setStatus(FriendshipStatus.PENDING);
        return mapToDTO(friendRepository.save(friends));
    }

    public void deleteInvitation(String requesterUsername, String receiverUsername) {
        User requester = getUser(requesterUsername);
        User receiver = getUser(receiverUsername);
        friendRepository.findByRequesterAndReceiver(requester, receiver)
                .ifPresent(friendRepository::delete);
    }

    public FriendsDTO acceptInvitation(String receiverUsername, String requesterUsername) {
        User receiver = getUser(receiverUsername);
        User requester = getUser(requesterUsername);
        Friends friends = friendRepository.findByRequesterAndReceiver(requester, receiver)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
        friends.setStatus(FriendshipStatus.ACCEPTED);
        return mapToDTO(friendRepository.save(friends));
    }

    public void deleteFriend(String requesterUsername, String receiverUsername) {
        User requester = getUser(requesterUsername);
        User receiver = getUser(receiverUsername);

        // Sprawdzamy przyjaźń w obu możliwych przypadkach (requester-receiver lub receiver-requester)
        Optional<Friends> friendOpt = friendRepository.findByRequesterAndReceiver(requester, receiver);

        if (!friendOpt.isPresent()) {
            // Jeśli nie znaleziono, sprawdzamy w drugą stronę (odwrócona relacja)
            friendOpt = friendRepository.findByRequesterAndReceiver(receiver, requester);
        }

        // Jeśli nadal nie znaleziono, rzucamy wyjątek
        Friends friend = friendOpt.orElseThrow(() -> new RuntimeException("Friendship not found"));

        // Usuwamy przyjaźń
        friendRepository.delete(friend);
    }



    private FriendsDTO mapToDTO(Friends f) {
        return new FriendsDTO(
                f.getId(),
                f.getRequester().getUsername(),
                f.getReceiver().getUsername(),
                f.getStatus(),
                f.getCreatedAt()
        );
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


}
