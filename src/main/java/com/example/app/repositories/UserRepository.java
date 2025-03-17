package com.example.app.repositories;

import com.example.app.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"roles"})
    List<User> findAll();
}