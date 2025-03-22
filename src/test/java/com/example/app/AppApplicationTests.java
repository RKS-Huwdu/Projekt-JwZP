package com.example.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.app.config.GoogleMapsApiConfig;
import com.google.maps.GeoApiContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
@SpringBootTest
class AppApplicationTests {

	/*@Test
	void contextLoads() {
	}*/

	@Autowired
	private GeoApiContext geoApiContext;

	@Test
	public void contextLoads() {
		assertNotNull(geoApiContext);
	}

}
