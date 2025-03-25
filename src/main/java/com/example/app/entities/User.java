package com.example.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "username"
                })
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false, unique = true)
        private String username;

        @Column(unique = true)
        @Email
        private String email;

        @Column(nullable = false)
        private String password;

        @ManyToMany
        @JoinTable(
                name = "user_roles",
                joinColumns = @JoinColumn(name = "user_id"),
                inverseJoinColumns = @JoinColumn(name = "role_id"),
                uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
        )
        @JsonIgnore
        private Set<Role> roles = new HashSet<>();

        @ManyToMany(mappedBy = "users")
        private Set<Place> places = new HashSet<>();

}
