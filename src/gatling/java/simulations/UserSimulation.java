package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class UserSimulation extends Simulation {
    private static final String BASE_URL = "http://localhost:8080";

    private static final String ADMIN_AUTH_HEADER = "Basic YWRtaW46YWRtaW4=";

    HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder adminScenario = scenario("Admin User Load Test")
            .exec(http("1. Get all users")
                    .get("/user/users")
                    .header("Authorization", ADMIN_AUTH_HEADER)
                    .check(status().is(200)))
            .pause(1)

            .exec(http("2. Get user by ID")
                    .get("/user/me")
                    .header("Authorization", ADMIN_AUTH_HEADER)
                    .check(status().in(200)));

    {
        setUp(
                adminScenario.injectOpen(
                        rampUsers(50).during(30)
                )
        ).protocols(httpProtocol);
    }
}