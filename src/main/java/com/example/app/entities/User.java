package com.example.app.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public record User(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        Long id,

        @Column(nullable = false, unique = true)
        String username,

        @Column(nullable = false, unique = true)
        String email,

        @Column(nullable = false)
        String password
) {}
