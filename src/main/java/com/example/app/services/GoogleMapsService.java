package com.example.app.services;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public GoogleMapsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public record GeoResult(double lat, double lng, String address, String city, String country) {}

    public Optional<GeoResult> geocodeAddress(String address) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("address", address)
                .queryParam("key", apiKey)
                .toUriString();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode results = response.getBody().get("results");

        if (results != null && results.size() > 0) {
            JsonNode location = results.get(0).get("geometry").get("location");
            double lat = location.get("lat").asDouble();
            double lng = location.get("lng").asDouble();

            String formattedAddress = results.get(0).get("formatted_address").asText();
            String city = "";
            String country = "";

            for (JsonNode comp : results.get(0).get("address_components")) {
                JsonNode types = comp.get("types");
                if (types.toString().contains("locality")) {
                    city = comp.get("long_name").asText();
                }
                if (types.toString().contains("country")) {
                    country = comp.get("long_name").asText();
                }
            }

            return Optional.of(new GeoResult(lat, lng, formattedAddress, city, country));
        }

        return Optional.empty();
    }

    public Optional<GeoResult> reverseGeocode(double lat, double lng) {
        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("latlng", lat + "," + lng)
                .queryParam("key", apiKey)
                .toUriString();

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode results = response.getBody().get("results");

        if (results != null && results.size() > 0) {
            String formattedAddress = results.get(0).get("formatted_address").asText();
            String city = "";
            String country = "";

            for (JsonNode comp : results.get(0).get("address_components")) {
                JsonNode types = comp.get("types");
                if (types.toString().contains("locality")) {
                    city = comp.get("long_name").asText();
                }
                if (types.toString().contains("country")) {
                    country = comp.get("long_name").asText();
                }
            }

            return Optional.of(new GeoResult(lat, lng, formattedAddress, city, country));
        }

        return Optional.empty();
    }
}