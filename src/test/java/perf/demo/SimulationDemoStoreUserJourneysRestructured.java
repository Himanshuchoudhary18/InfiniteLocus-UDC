package perf.demo;

import utilities.SessionId;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Choice;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import perf.demo.pageObject.Catalog;
import perf.demo.pageObject.Checkout;
import perf.demo.pageObject.CmsPage;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class SimulationDemoStoreUserJourneysRestructured extends Simulation {
    private static final String DOMAIN = "localhost:8080";
    private static final HttpProtocolBuilder HTTP_PROTOCOL = http.baseUrl("http://" + DOMAIN);
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "5"));
    private static final Duration RAMP_DURATION = Duration.ofSeconds(Integer.parseInt(System.getProperty("RAMP_DURATION", "10")));
    private static final Duration TEST_DURATION = Duration.ofSeconds(Integer.parseInt(System.getProperty("TEST_DURATION", "60")));
    @Override
    public void before() {
        System.out.println("Welcome to the Gatling Demo Store! @Author: Sachin Bhardwaj");
        System.out.println("-----------------------------------------------------------");
//        System.out.printf(
//                "Starting the test with %d users and a ramp-up duration of %d seconds with total duration %d seconds --%n",
//                USER_COUNT,
//                RAMP_DURATION.getSeconds(),
//                TEST_DURATION.getSeconds()
//        );
        System.out.println("-----------------------------------------------------------");
    }
    @Override
    public void after(){
        System.out.println("Test completed");
    }

    private static final ChainBuilder initSession =
            exec(flushCookieJar())
                    .exec(session -> session.set("randomNumber", Optional.of(ThreadLocalRandom.current().nextInt())))
//                    .exec(session -> session.set("customerLoggedIn", false))
//                    .exec(session -> session.set("cartTotal", 0))
                    .exec(addCookie(Cookie("sessionID", SessionId.random()).withDomain(DOMAIN)));

    private static class UserJourneys {
        private static final Duration MIN_PAUSE = Duration.ofMillis(100);
        private static final Duration MAX_PAUSE = Duration.ofMillis(500);
        private static final ChainBuilder browseStore =
                exec(initSession)
                        .exec(CmsPage.homePage)
                        .pause(MAX_PAUSE)
                        .exec(CmsPage.aboutUs)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .repeat(5)
                        .on(
                                exec(Catalog.Category.view)
                                        .pause(MIN_PAUSE, MAX_PAUSE)
                                        .exec(Catalog.Product.view)
                        );
        private static final ChainBuilder abandonCart =
                initSession
                        .exec(CmsPage.homePage)
                        .pause(MAX_PAUSE)
                        .exec(Catalog.Category.view)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Catalog.Product.view)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Catalog.Product.addProductToCart);
        private static final ChainBuilder completePurchase =
                initSession
                        .exec(CmsPage.homePage)
                        .pause(MAX_PAUSE)
                        .exec(Catalog.Category.view)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Catalog.Product.view)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Catalog.Product.addProductToCart)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Checkout.viewCart)
                        .pause(MIN_PAUSE, MAX_PAUSE)
                        .exec(Checkout.checkout);
    }

    private static class Scenarios {
        private static final ScenarioBuilder defaultPurchase =
                scenario("Default Load Test")
                        .during(TEST_DURATION)
                        .on(
                                randomSwitch().on(
                                        Choice.withWeight(75.0, exec(UserJourneys.browseStore)),
                                        Choice.withWeight(15.0, exec(UserJourneys.abandonCart)),
                                        Choice.withWeight(10.0, exec(UserJourneys.completePurchase))
                                )
                        );
        private static final ScenarioBuilder highPurchase =
                scenario("High Purchase Load Test")
                        .during(TEST_DURATION)
                        .on(
                                randomSwitch().on(
                                        Choice.withWeight(25.0, exec(UserJourneys.browseStore)),
                                        Choice.withWeight(25.0, exec(UserJourneys.abandonCart)),
                                        Choice.withWeight(50.0, exec(UserJourneys.completePurchase))
                                )
                        );
    }

    {
        setUp(
                Scenarios.defaultPurchase.injectOpen(
                                rampUsers(USER_COUNT).during(RAMP_DURATION)).protocols(HTTP_PROTOCOL)
                        .andThen(
                                Scenarios.highPurchase.injectOpen(
                                        rampUsers(USER_COUNT).during(TEST_DURATION)).protocols(HTTP_PROTOCOL)
                        ));
    }
}
