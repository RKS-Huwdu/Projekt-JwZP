package com.example.app.services;

import com.google.maps.GeoApiContext;
import com.google.maps.GeolocationApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeolocationPayload;
import com.google.maps.model.LatLng;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapsService{
    private final GeoApiContext context;

    @Autowired
    public GoogleMapsService(GeoApiContext context){
        this.context = context;
    }

    public LatLng getUserLocation() throws IOException, InterruptedException, ApiException {
        return GeolocationApi.geolocate(context,new GeolocationPayload()).await().location;
    }



}