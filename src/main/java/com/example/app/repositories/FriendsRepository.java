package com.example.app.repositories;

import com.example.app.entities.Friends;
import com.example.app.entities.FriendshipStatus;
import com.example.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    List<Friends> findByRequesterOrReceiver(User requester, User receiver);
    List<Friends> findByReceiverAndStatus(User receiver, FriendshipStatus status);
    Optional<Friends> findByRequesterAndReceiver(User requester, User receiver);
    void deleteByRequesterAndReceiver(User requester, User receiver);
}

