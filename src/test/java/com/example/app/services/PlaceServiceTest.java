package com.example.app.services;

import com.example.app.dtos.CreatePlaceDTO;
import com.example.app.dtos.PlaceDTO;
import com.example.app.dtos.UpdatePlaceDTO;
import com.example.app.entities.*;
import com.example.app.exception.*;
import com.example.app.repositories.CategoryRepository;
import com.example.app.repositories.PlaceRepository;
import com.example.app.repositories.UserRepository;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

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

    @Test
    public void findAll_shouldReturnAllPlaces() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        Category category = new Category();
        category.setName("category");
        category.setId(1L);

        Place place1 = new Place();
        place1.setCategory(category);
        place1.setId(1L);
        place1.setName("place1");
        place1.setUser(user);

        Place place2 = new Place();
        place2.setId(2L);
        place2.setPublic(false);
        place2.setCategory(category);
        place2.setName("place2");
        place2.setUser(user);

        Place place3 = new Place();
        place3.setId(3L);
        place3.setName("place3");
        place3.setCategory(category);
        place3.setUser(user);



        when(placeRepository.findAllByUser_Username(username)).thenReturn(List.of(place1,place2,place3));;

        List<PlaceDTO> places = placeService.findAll(username);

        assertThat(places).isNotNull();
        assertThat(places.size()).isEqualTo(3);
        assertThat(places.get(0).name()).isEqualTo(place1.getName());
        assertThat(places.get(0).id()).isEqualTo(place1.getId());
        assertThat(places.get(0).category()).isEqualTo(place1.getCategory().getName());

        assertThat(places.get(1).name()).isEqualTo(place2.getName());
        assertThat(places.get(1).id()).isEqualTo(place2.getId());
        assertThat(places.get(1).category()).isEqualTo(place2.getCategory().getName());


        assertThat(places.get(2).name()).isEqualTo(place3.getName());
        assertThat(places.get(2).id()).isEqualTo(place3.getId());
        assertThat(places.get(2).category()).isEqualTo(place3.getCategory().getName());

        verify(placeRepository).findAllByUser_Username(username);
    }

    @Test
    public void findAllPrivate_shouldReturnAllPrivatePlaces() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        Category category = new Category();
        category.setName("category");
        category.setId(1L);

        Place place1 = new Place();
        place1.setCategory(category);
        place1.setId(1L);
        place1.setName("place1");
        place1.setUser(user);

        Place place2 = new Place();
        place2.setId(2L);
        place2.setPublic(false);
        place2.setCategory(category);
        place2.setName("place2");
        place2.setUser(user);

        Place place3 = new Place();
        place3.setId(3L);
        place3.setName("place3");
        place3.setCategory(category);
        place3.setUser(user);

        when(placeRepository.findPrivatePlacesByUsername(username)).thenReturn(List.of(place1,place3));;

        List<PlaceDTO> places = placeService.findAllPrivate(username);

        assertThat(places).isNotNull();
        assertThat(places.size()).isEqualTo(2);
        assertThat(places.get(0).name()).isEqualTo(place1.getName());
        assertThat(places.get(0).id()).isEqualTo(place1.getId());
        assertThat(places.get(0).category()).isEqualTo(place1.getCategory().getName());

        assertThat(places.get(1).name()).isEqualTo(place3.getName());
        assertThat(places.get(1).id()).isEqualTo(place3.getId());
        assertThat(places.get(1).category()).isEqualTo(place3.getCategory().getName());

        verify(placeRepository).findPrivatePlacesByUsername(username);
    }

    @Test
    public void findFriendPlaces_shouldReturnAllFriendPlaces() {
        String username = "username";
        String friendUsername = "friend";
        User friend = new User();
        friend.setUsername(friendUsername);

        Category category = new Category();
        category.setName("category");
        category.setId(1L);

        Place place1 = new Place();
        place1.setCategory(category);
        place1.setId(1L);
        place1.setName("place1");
        place1.setUser(friend);

        Place place2 = new Place();
        place2.setId(2L);
        place2.setPublic(false);
        place2.setCategory(category);
        place2.setName("place2");
        place2.setUser(friend);

        Place place3 = new Place();
        place3.setId(3L);
        place3.setName("place3");
        place3.setCategory(category);
        place3.setUser(friend);

        when(placeRepository.findPublicPlacesByUsername(friendUsername)).thenReturn(List.of(place1,place3));
        when(friendService.isFriendWith(username,friendUsername)).thenReturn(true);

        List<PlaceDTO> places = placeService.findFriendPlaces(username,friendUsername);

        assertThat(places).isNotNull();
        assertThat(places.size()).isEqualTo(2);
        assertThat(places.get(0).name()).isEqualTo(place1.getName());
        assertThat(places.get(0).id()).isEqualTo(place1.getId());
        assertThat(places.get(0).category()).isEqualTo(place1.getCategory().getName());

        assertThat(places.get(1).name()).isEqualTo(place3.getName());
        assertThat(places.get(1).id()).isEqualTo(place3.getId());
        assertThat(places.get(1).category()).isEqualTo(place3.getCategory().getName());

        verify(placeRepository).findPublicPlacesByUsername(friendUsername);
        verify(friendService).isFriendWith(username,friendUsername);
    }

    @Test
    public void findFriendPlaces_shouldThrowFriendNotFoundException() {
        String username = "username";
        String friendUsername = "friend";

        when(friendService.isFriendWith(username,friendUsername)).thenReturn(false);

        assertThatThrownBy(() -> placeService.findFriendPlaces(username,friendUsername))
                .isInstanceOf(FriendNotFoundException.class)
                .hasMessageContaining("You have no friend named: " + friendUsername);

    }

    @Test
    public void findNearestPlace_shouldReturnPlace() {
        String username = "username";
        User user = new User();
        user.setUsername(username);

        Category category = new Category();
        category.setName("category");
        category.setId(1L);

        Place place1 = new Place();
        place1.setCategory(category);
        place1.setId(1L);
        place1.setName("place1");
        place1.setUser(user);
        place1.setLatitude(0.1);
        place1.setLongitude(0.1);

        Place place2 = new Place();
        place2.setId(2L);
        place2.setPublic(false);
        place2.setCategory(category);
        place2.setName("place2");
        place2.setUser(user);
        place2.setLatitude(0.5);
        place2.setLongitude(0.5);

        Place place3 = new Place();
        place3.setId(3L);
        place3.setName("place3");
        place3.setCategory(category);
        place3.setUser(user);
        place3.setLatitude(0.8);
        place3.setLongitude(0.8);

        when(placeRepository.findAllByUser_Username(username)).thenReturn(List.of(place1,place2,place3));

        PlaceDTO placeDTO = placeService.findNearestPlace(username,0,0);
        assertThat(placeDTO).isNotNull();
        assertThat(placeDTO.id()).isEqualTo(place1.getId());
        assertThat(placeDTO.category()).isEqualTo(place1.getCategory().getName());
        assertThat(placeDTO.latitude()).isEqualTo(place1.getLatitude());
        assertThat(placeDTO.longitude()).isEqualTo(place1.getLongitude());
    }

    @Test
    public void findNearestPlace_shouldThrowPlaceNotFoundException() {
        String username = "username";


        when(placeRepository.findAllByUser_Username(username)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> placeService.findNearestPlace(username,0,0))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("User has no saved places");

    }

    @Test
    public void findNearestPlaceByCategory_shouldReturnPlace() {
        String username = "username";
        User user = new User();
        user.setUsername(username);


        Category category = new Category();
        category.setName("category");
        category.setId(1L);

        Place place1 = new Place();
        place1.setCategory(category);
        place1.setId(1L);
        place1.setName("place1");
        place1.setUser(user);
        place1.setLatitude(0.8);
        place1.setLongitude(0.8);

        Place place2 = new Place();
        place2.setId(2L);
        place2.setPublic(false);
        place2.setCategory(category);
        place2.setName("place2");
        place2.setUser(user);
        place2.setLatitude(0.5);
        place2.setLongitude(0.5);

        when(placeRepository.findAllByCategoryAndUser_Username(category.getName(),username)).thenReturn(List.of(place1,place2));

        PlaceDTO placeDTO = placeService.findNearestPlace(username,0,0,category.getName());
        assertThat(placeDTO).isNotNull();
        assertThat(placeDTO.id()).isEqualTo(place2.getId());
        assertThat(placeDTO.category()).isEqualTo(place2.getCategory().getName());
        assertThat(placeDTO.latitude()).isEqualTo(place2.getLatitude());
        assertThat(placeDTO.longitude()).isEqualTo(place2.getLongitude());
    }

    @Test
    public void findNearestPlaceByCategory_shouldThrowPlaceNotFoundException() {
        String username = "username";
        String categoryName = "category";


        when(placeRepository.findAllByCategoryAndUser_Username(categoryName,username)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> placeService.findNearestPlace(username,0,0,categoryName))
                .isInstanceOf(PlaceNotFoundException.class)
                .hasMessageContaining("User has no saved places of category: " + categoryName);

    }
}

