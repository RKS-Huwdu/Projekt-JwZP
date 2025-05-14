package com.example.app.repositories;

import com.example.app.entities.Place;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    @EntityGraph(attributePaths = {"category"})
    Optional<Place> findByIdAndUser_Username(Long id, String username);

    @EntityGraph(attributePaths = {"category"})
    List<Place> findAllByUser_Username(String username);

    @Query("SELECT p FROM Place p JOIN p.sharedWith u WHERE u.id = :userId")
    @EntityGraph(attributePaths = {"category"})
    List<Place>  findAllSharedByUserId(@Param("userId") Long userId);

    @Query("SELECT p FROM Place p JOIN p.sharedWith u WHERE u.username = :username")
    @EntityGraph(attributePaths = {"category"})
    List<Place>  findAllSharedByUsername(@Param("username") String username);


    @Query("SELECT p FROM Place p WHERE p.user.username = :username AND p.isPublic = false")
    @EntityGraph(attributePaths = {"category"})
    List<Place> findPrivatePlacesByUsername(@Param("username") String username);

    @Query("SELECT p FROM Place p WHERE p.user.username = :username AND p.isPublic = true")
    @EntityGraph(attributePaths = {"category"})
    List<Place> findPublicPlacesByUsername(@Param("username") String username);
}
