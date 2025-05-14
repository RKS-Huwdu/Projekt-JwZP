package com.example.app.dto;

import com.example.app.dtos.UserDTO;
import com.example.app.entities.Place;
import com.example.app.entities.Role;
import com.example.app.entities.RoleName;
import com.example.app.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class UserDTOTest {
    @Test
    void shouldMapUserToUserDTO() {
        Role role = new Role(RoleName.ADMIN);
        Place place = new Place();
        place.setName("Park");

        User user = new User();
        user.setId(1L);
        user.setUsername("john");
        user.setEmail("john@example.com");
        user.setRoles(Set.of(role));
        user.setPlaces(Set.of(place));

        UserDTO dto = UserDTO.fromEntity(user);

        assertEquals(1L, dto.id());
        assertEquals("john", dto.username());
        assertEquals("john@example.com", dto.email());
        assertTrue(dto.roles().contains("ADMIN"));
        assertTrue(dto.places().contains("Park"));
    }
}
