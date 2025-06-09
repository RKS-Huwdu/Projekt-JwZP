package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CreatePlaceSimulation extends Simulation {

    private static final int PLACES_PER_EXEC = 5;

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Create Places for User")
            .repeat(PLACES_PER_EXEC).on(
                    exec(session -> {
                        double lat = ThreadLocalRandom.current().nextDouble(-90, 90);
                        double lon = ThreadLocalRandom.current().nextDouble(-180, 180);
                        String placeName = "Place_" + System.nanoTime();

                        return session
                                .set("placeName", placeName)
                                .set("category", "Park")
                                .set("latitude", lat)
                                .set("longitude", lon)
                                .set("note", "Some note");
                    }).exec(http("Create Place")
                            .post("/places")
                            .basicAuth("admin", "admin")
                            .body(StringBody(session -> String.format("""
                    {
                        "name":"%s",
                        "category":"%s",
                        "latitude":%s,
                        "longitude":%s,
                        "note":"%s"
                    }
                    """,
                                    session.getString("placeName"),
                                    session.getString("category"),
                                    session.getDouble("latitude"),
                                    session.getDouble("longitude"),
                                    session.getString("note")
                            )))
                            .check(status().is(201))
                    )
            );

    {
        setUp(
                scn.injectOpen(
                        rampUsers(20).during(5)
                )
        ).protocols(httpProtocol);
    }
}

