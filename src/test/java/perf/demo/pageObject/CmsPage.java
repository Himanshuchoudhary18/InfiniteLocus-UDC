package perf.demo.pageObject;

import io.gatling.javaapi.core.ChainBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public final class CmsPage {
    public static final ChainBuilder homePage =
            exec(http("Load Home Page")
                    .get("/")
                    .check(css("#_csrf", "content").saveAs("csrfValue"))
                    .check(regex("<title>Gatling Demo-Store</title>").exists()));
    public static final ChainBuilder aboutUs =
            exec(http("Load About Us Page")
                    .get("/about-us")
                    .check(substring("About Us")));
}
