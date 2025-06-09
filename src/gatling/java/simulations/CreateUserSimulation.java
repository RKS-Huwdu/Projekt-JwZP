package simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class CreateUserSimulation extends Simulation {

    private static final int USER_COUNT = 100;

    Iterator<Map<String, Object>> userFeeder = Stream.iterate(1, i -> i + 1)
            .limit(USER_COUNT)
            .map(i -> Map.<String, Object>of(
                    "username", "user" + i,
                    "email", "user" + i + "@example.com",
                    "password", "Password123!"
            ))
            .iterator();

    HttpProtocolBuilder httpProtocol = http.baseUrl("http://localhost:8080")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    ScenarioBuilder scn = scenario("Create Multiple Unique Users")
            .feed(userFeeder)
            .exec(http("Create User")
                    .post("/register")
                    .body(StringBody(session -> String.format("""
                {
                  "username": "%s",
                  "email": "%s",
                  "password": "%s"
                }
                """,
                            session.getString("username"),
                            session.getString("email"),
                            session.getString("password")
                    )))
                    .check(status().is(201))
            )
            .exec(http("Delete User")
                    .delete("/user/me")
                    .basicAuth("#{username}", "#{password}")
                    .check(status().in(200))
            );;

    {
        setUp(
                scn.injectOpen(
                        rampUsers(USER_COUNT).during(30)
                )
        ).protocols(httpProtocol);
    }
}
