package com.example.app.repositories;

import com.example.app.entities.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @Query("SELECT p FROM Place p JOIN p.users u WHERE u.id = :userId")
    List<Place>  findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Place p JOIN p.users u WHERE u.id = :userId AND p.id = :placeId")
    Optional<Place> findByUserIdAndPlaceId(@Param("userId") Long userId, @Param("placeId") Long placeId);

    Optional<Place> findByName(String name);

    @Query("SELECT p FROM Place p LEFT JOIN FETCH p.users WHERE p.id = :placeId")
    Optional<Place> findByIdWithUsers(@Param("placeId") Long placeId);
}
