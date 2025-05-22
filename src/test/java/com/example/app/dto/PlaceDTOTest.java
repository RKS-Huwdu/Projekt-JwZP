package com.example.app.dto;

import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PlaceDTOTest {
    @Test
    void shouldMapFriendshipToDTO() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Cafe");

        Place place = new Place();
        place.setId(1L);
        place.setName("Starbucks");
        place.setCategory(category);
        place.setLatitude(52.2336236);
        place.setLongitude(21.0020490);
        place.setAddress("Emilii Plater 53, 00-113");
        place.setCountry("Poland");
        place.setCity("Warsaw");
        place.setNote("best coffee in Warsaw");
        place.setPublic(true);
        OffsetDateTime postDate = OffsetDateTime.now();
        place.setPostDate(postDate);

        PlaceDTO dto = PlaceDTO.fromEntity(place);
        assertEquals(1L, dto.id());
        assertEquals("Starbucks", dto.name());
        assertEquals("Cafe", dto.category());
        assertEquals(52.2336236, dto.latitude());
        assertEquals(21.0020490, dto.longitude());
        assertEquals("Emilii Plater 53, 00-113", dto.address());
        assertEquals("Poland", dto.country());
        assertEquals("Warsaw", dto.city());
        assertEquals("best coffee in Warsaw", dto.note());
        assertTrue(dto.isPublic());
        assertEquals(postDate, dto.postDate());
    }
}
