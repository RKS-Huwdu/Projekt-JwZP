package com.example.app.dtos;

import com.example.app.entities.Place;
import com.example.app.entities.User;

import java.util.Set;
import java.util.stream.Collectors;


public record UserDTO(
        Long id,
        String username,
        String email,
        Set<String> roles,
        Set<String> places)
{
    public static UserDTO fromEntity(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()),
                user.getPlaces().stream()
                        .map(Place::getName)
                        .collect(Collectors.toSet())
        );
    }
}
