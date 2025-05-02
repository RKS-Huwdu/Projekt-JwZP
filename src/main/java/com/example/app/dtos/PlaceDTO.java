package com.example.app.dtos;

import com.example.app.entities.Place;
import com.example.app.entities.User;

import java.util.Set;
import java.util.stream.Collectors;

public record PlaceDTO(Long id,
                       String name,
                       String category,
                       double latitude,
                       double longitude,
                       String address,
                       String country,
                       String city,
                       String note) {
    public static PlaceDTO fromEntity(Place place) {
        return new PlaceDTO(
                place.getId(),
                place.getName(),
                place.getCategory().getName(),
                place.getLatitude(),
                place.getLongitude(),
                place.getAddress(),
                place.getCountry(),
                place.getCity(),
                place.getNote()

        );
    }
}

