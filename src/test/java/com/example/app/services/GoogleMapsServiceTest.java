package com.example.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@SpringBootTest
public class GoogleMapsServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GoogleMapsService googleMapsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void geocodeAddress_validResponse_returnGeoResult() throws Exception {
        String address = "Warsaw, Poland";
        String json = """
        {
          "results": [
            {
              "formatted_address": "Warsaw, Poland",
              "geometry": {
                "location": {
                  "lat": 52.2297,
                  "lng": 21.0122
                }
              },
              "address_components": [
                {
                  "long_name": "Warsaw",
                  "types": ["locality"]
                },
                {
                  "long_name": "Poland",
                  "types": ["country"]
                }
              ]
            }
          ]
        }
        """;
        JsonNode jsonNode = objectMapper.readTree(json);
        ResponseEntity<JsonNode> response = new ResponseEntity<>(jsonNode, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);

        Optional<GoogleMapsService.GeoResult> result = googleMapsService.geocodeAddress(address);

        assertTrue(result.isPresent());
        assertEquals(52.2297, result.get().lat());
        assertEquals(21.0122, result.get().lng());
        assertEquals("Warsaw, Poland", result.get().address());
        assertEquals("Warsaw", result.get().city());
        assertEquals("Poland", result.get().country());
    }

    @Test
    void geocodeAddress_noResults_returnEmpty() throws Exception {
        String json = """
        {
          "results": []
        }
        """;
        JsonNode jsonNode = objectMapper.readTree(json);
        ResponseEntity<JsonNode> response = new ResponseEntity<>(jsonNode, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);

        Optional<GoogleMapsService.GeoResult> result = googleMapsService.geocodeAddress("Some Address");

        assertTrue(result.isEmpty());
    }

    @Test
    void reverseGeocode_validResponse_returnGeoResult() throws Exception {
        double lat = 52.2297;
        double lng = 21.0122;
        String json = """
        {
          "results": [
            {
              "formatted_address": "Warsaw, Poland",
              "address_components": [
                {
                  "long_name": "Warsaw",
                  "types": ["locality"]
                },
                {
                  "long_name": "Poland",
                  "types": ["country"]
                }
              ]
            }
          ]
        }
        """;

        JsonNode jsonNode = objectMapper.readTree(json);
        ResponseEntity<JsonNode> response = new ResponseEntity<>(jsonNode, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);

        Optional<GoogleMapsService.GeoResult> result = googleMapsService.reverseGeocode(lat, lng);

        assertTrue(result.isPresent());
        assertEquals(lat, result.get().lat());
        assertEquals(lng, result.get().lng());
        assertEquals("Warsaw, Poland", result.get().address());
        assertEquals("Warsaw", result.get().city());
        assertEquals("Poland", result.get().country());
    }

    @Test
    void reverseGeocode_noResults_ReturnEmpty() throws Exception {
        String json = """
        {
          "results": []
        }
        """;
        JsonNode jsonNode = objectMapper.readTree(json);
        ResponseEntity<JsonNode> response = new ResponseEntity<>(jsonNode, HttpStatus.OK);

        when(restTemplate.getForEntity(anyString(), eq(JsonNode.class))).thenReturn(response);

        Optional<GoogleMapsService.GeoResult> result = googleMapsService.reverseGeocode(0, 0);

        assertTrue(result.isEmpty());
    }
}
