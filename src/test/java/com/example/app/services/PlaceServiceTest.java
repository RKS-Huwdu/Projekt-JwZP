package com.example.app.services;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.dtos.UpdatePlaceDTO;
import com.example.app.entities.*;
import com.example.app.exception.*;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
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
    void savePlace_shouldReturnPlaceDTO(){
        String username = "username";

        Category category= new Category();
        String categoryName = "category";
        category.setId(1L);
        category.setName(categoryName);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(placeRepository.save(any(Place.class))).thenReturn(new Place());
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(Instant.now());

        CreatePlaceDTO dto = new CreatePlaceDTO("place",categoryName,37.4220656,-122.0840897,"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA","note",true);
        PlaceDTO result = placeService.save(username,dto);
        assertNotNull(result);
        assertThat(result.name()).isEqualTo(dto.name());
        assertThat(result.category()).isEqualTo(dto.category());
        assertThat(result.latitude()).isEqualTo(dto.latitude());
        assertThat(result.longitude()).isEqualTo(dto.longitude());
        assertThat(result.address()).isEqualTo(dto.address());
        assertThat(result.note()).isEqualTo(dto.note());
        assertThat(result.isPublic()).isEqualTo(dto.isPublic());

        verify(userRepository).findByUsername(username);
        verify(categoryRepository).findByName(categoryName);
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    void savePlace_placeAlreadyExists_shouldThrowException(){
        String username = "username";
        String categoryName = "category";
        String placeName = "place";
        String note = "note";
        String address = "address";

        CreatePlaceDTO dto = new CreatePlaceDTO(placeName,categoryName,0,0,address,note,true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(placeRepository.findByNameAndUser_Username(placeName,username)).thenReturn(Optional.of(new Place()));

        assertThatThrownBy(() -> placeService.save(username,dto))
                .isInstanceOf(PlaceAlreadyExistsException.class)
                .hasMessageContaining("Place already exists: " + placeName);
    }

    @Test
    void savePlace_categoryNotFound_shouldThrowException(){
        String username = "username";
        String categoryName = "category";
        String placeName = "place";
        String note = "note";
        String address = "address";

        CreatePlaceDTO dto = new CreatePlaceDTO(placeName,categoryName,0,0,address,note,true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(placeRepository.findByNameAndUser_Username(placeName,username)).thenReturn(Optional.empty());
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.save(username,dto))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found: " + categoryName);
    }


    @Test
    void deletePlaceById_shouldDeletePlace(){
        User user = new User();
        user.setUsername("username");
        Place place = new Place();
        place.setId(1L);
        place.setUser(user);

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));
        doNothing().when(placeRepository).delete(place);

        placeService.deleteById(user.getUsername(),place.getId());

        verify(placeRepository).findById(place.getId());
        verify(placeRepository).delete(place);
    }

    @Test
    void deletePlaceById_userDoesNotOwnPlace_shouldThrowException(){
        User user = new User();
        user.setUsername("username1");
        Place place = new Place();
        place.setId(1L);
        place.setUser(user);
        String username2 = "username2";

        when(placeRepository.findById(place.getId())).thenReturn(Optional.of(place));

        assertThatThrownBy(() -> placeService.deleteById(username2,place.getId()))
                .isInstanceOf(ResourceOwnershipException.class)
                .hasMessageContaining("User does not own this place");

    }

    @Test
    void sharePlace_shouldSharePlace(){
        String senderUsername = "senderUsername";
        User receiver = new User();
        receiver.setUsername("receiverUsername");
        Place place = new Place();
        place.setId(1L);

        when(userRepository.findByUsername(receiver.getUsername())).thenReturn(Optional.of(receiver));
        when(placeRepository.findByIdAndUser_Username(place.getId(),senderUsername)).thenReturn(Optional.of(place));
        when(placeRepository.save(any(Place.class))).thenReturn(place);
        when(userRepository.save(any(User.class))).thenReturn(receiver);

        placeService.share(senderUsername,receiver.getUsername(),place.getId());
        assertThat(receiver.getSharedPlaces())
                .contains(place);
        verify(placeRepository).findByIdAndUser_Username(place.getId(),senderUsername);
        verify(userRepository).findByUsername(receiver.getUsername());
        verify(userRepository).save(receiver);
        verify(placeRepository).save(place);
    }

    @Test
    void sharePlace_placeNotFound_shouldThrowException(){
        String senderUsername = "senderUsername";
        User receiver = new User();
        receiver.setUsername("receiverUsername");
        Long placeId = 1L;

        when(placeRepository.findByIdAndUser_Username(placeId,senderUsername)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.share(senderUsername,receiver.getUsername(),placeId))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("Place not found or does not belong to user");

    }

    @Test
    void sharePlace_receiverDoesNotExist_shouldThrowException(){
        String senderUsername = "senderUsername";
        User receiver = new User();
        receiver.setUsername("receiverUsername");
        Place place = new Place();
        place.setId(1L);

        when(placeRepository.findByIdAndUser_Username(place.getId(),senderUsername)).thenReturn(Optional.of(place));
        when(userRepository.findByUsername(receiver.getUsername())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.share(senderUsername,receiver.getUsername(),place.getId()))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found: " + receiver.getUsername());

    }

    @Test
    void updatePlace_shouldUpdatePlace(){
        String username = "username";
        Place place = new Place();
        place.setId(1L);
        Category category = new Category();
        String categoryName = "category";
        category.setId(1L);
        category.setName(categoryName);

        UpdatePlaceDTO updatePlaceDTO = new UpdatePlaceDTO("place",categoryName,37.4220656,-122.0840897,"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA","note");

        when(placeRepository.findByIdAndUser_Username(place.getId(),username)).thenReturn(Optional.of(place));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(category));
        when(placeRepository.save(any(Place.class))).thenReturn(place);

        PlaceDTO result = placeService.update(username,place.getId(),updatePlaceDTO);
        assertThat(result.name()).isEqualTo(updatePlaceDTO.name());
        assertThat(result.category()).isEqualTo(updatePlaceDTO.category());
        assertThat(result.latitude()).isEqualTo(updatePlaceDTO.latitude());
        assertThat(result.longitude()).isEqualTo(updatePlaceDTO.longitude());
        assertThat(result.address()).isEqualTo(updatePlaceDTO.address());
        assertThat(result.note()).isEqualTo(updatePlaceDTO.note());

        verify(placeRepository).findByIdAndUser_Username(place.getId(),username);
        verify(categoryRepository).findByName(categoryName);
        verify(placeRepository).save(place);
    }

    @Test
    void updatePlace_placeNotFound_shouldThrowException(){
        String username = "username";
        String categoryName = "category";
        Long placeId = 1L;

        UpdatePlaceDTO updatePlaceDTO = new UpdatePlaceDTO("place",categoryName,37.4220656,-122.0840897,"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA","note");

        when(placeRepository.findByIdAndUser_Username(placeId,username)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.update(username,placeId,updatePlaceDTO))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("Place not found or does not belong to user");

    }

    @Test
    void updatePlace_categoryNotFound_shouldThrowException(){
        String username = "username";
        String categoryName = "category";
        Long placeId = 1L;

        UpdatePlaceDTO updatePlaceDTO = new UpdatePlaceDTO("place",categoryName,37.4220656,-122.0840897,"1600 Amphitheatre Parkway, Mountain View, CA 94043, USA","note");

        when(placeRepository.findByIdAndUser_Username(placeId,username)).thenReturn(Optional.of(new Place()));
        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> placeService.update(username,placeId,updatePlaceDTO))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessageContaining("Category not found" + categoryName);

    }
}

