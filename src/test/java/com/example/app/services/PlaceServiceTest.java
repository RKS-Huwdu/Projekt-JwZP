package com.example.app.services;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.entities.*;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Clock;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class PlaceServiceTest {
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FriendService friendService;
    @Mock
    private GoogleMapsService googleMapsService;
    @Mock
    private Clock clock;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void addPlace_shouldReturnPlaceDTO(){
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email@email.com");
        user.setPassword("encodedPassword");
        Role freeUserRole = new Role(RoleName.FREE_USER);
        user.setRoles(Set.of(freeUserRole));
        userRepository.save(user);

        Category category= new Category();
        category.setId(1L);
        category.setName("category");
        categoryRepository.save(category);

        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
        when(categoryRepository.findByName("category")).thenReturn(Optional.of(category));
        when(placeRepository.save(any(Place.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreatePlaceDTO dto = new CreatePlaceDTO("place","category",37.4220656,-122.0840897,"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA","note",true);
        PlaceDTO result = placeService.save("username",dto);
        assertNotNull(result);
        assertThat(result.name()).isEqualTo(dto.name());
        assertThat(result.category()).isEqualTo(dto.category());
        assertThat(result.latitude()).isEqualTo(dto.latitude());
        assertThat(result.longitude()).isEqualTo(dto.longitude());
        assertThat(result.address()).isEqualTo(dto.address());
        assertThat(result.note()).isEqualTo(dto.note());
        assertThat(result.isPublic()).isEqualTo(dto.isPublic());

        verify(userRepository).findByUsername("username");
        verify(categoryRepository).findByName("category");
        verify(placeRepository).save(any(Place.class));
    }
}
