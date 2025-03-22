package com.example.app.config;

import com.google.maps.GeoApiContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleMapsApiConfig{

    @Value("${google.maps.api.key}")
    private String apiKey;

    @Bean
    GeoApiContext geoApiContext(){
        return new GeoApiContext.Builder().apiKey(apiKey).build();
    }
}

