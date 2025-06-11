package com.example.app.endpoints;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.*;
import com.example.app.exception.PlaceNotFoundException;
import com.example.app.security.CustomUserDetails;
import com.example.app.services.PlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    private CustomUserDetails customUser;
    private PlaceDTO testPlace;
    private final String testUsername = "testUser";
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName(RoleName.FREE_USER);
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User userEntity = new User();
        userEntity.setUsername(testUsername);
        userEntity.setPassword("password");
        userEntity.setId(1L);
        userEntity.setRoles(roles);
        userEntity.setEmail("test@example.com");

        Place placeEntity = new Place();
        placeEntity.setCategory(new Category(20, "category"));
        testPlace = PlaceDTO.fromEntity(placeEntity);

        customUser = new CustomUserDetails(userEntity);
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldReturnAllPlaces() throws Exception {
        Mockito.when(placeService.findAll(testUsername)).thenReturn(List.of());

        mockMvc.perform(get("/places")
                .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnPrivatePlaces() throws Exception {
        Mockito.when(placeService.findAllPrivate("testuser")).thenReturn(List.of());

        mockMvc.perform(get("/places/private")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnFriendPlaces() throws Exception {
        Mockito.when(placeService.findFriendPlaces("testuser", "friend"))
                .thenReturn(List.of());

        mockMvc.perform(get("/places/friend/friend")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnPlaceById() throws Exception {
        Mockito.when(placeService.findById("testuser", 1L)).thenReturn(testPlace);

        mockMvc.perform(get("/places/1")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreatePlace() throws Exception {
        CreatePlaceDTO dto = new CreatePlaceDTO("Place", "Cat", 0, 0, "", "", false);

        Mockito.when(placeService.save(eq("testuser"), any(CreatePlaceDTO.class)))
                .thenReturn(testPlace);

        mockMvc.perform(post("/places")
                        .with(user(customUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldDeletePlace() throws Exception {
        mockMvc.perform(delete("/places/1")
                        .with(user(customUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFindNearestPlace() throws Exception {
        Mockito.when(placeService.findNearestPlace("testuser", 50.0, 20.0))
                .thenReturn(testPlace);

        mockMvc.perform(get("/places/nearest")
                        .param("latitude", "50.0")
                        .param("longitude", "20.0")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnSharedPlaces() throws Exception {
        Mockito.when(placeService.findAllSharedPlaces("testuser"))
                .thenReturn(List.of());

        mockMvc.perform(get("/places/shared")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldSharePlace() throws Exception {
        mockMvc.perform(post("/places/1/share/receiver")
                        .with(user(customUser)))
                .andExpect(status().isOk());
    }
}

