package perf.demo.pageObject;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.FeederBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public final class Product {
    private static final FeederBuilder<Object> jsonFeederProductFeeder = jsonFile("perfDemoData/productDetails.json").random();

    public static final ChainBuilder view =
            feed(jsonFeederProductFeeder)
                    .exec(http("View Product - #{name}")
                            .get("/product/#{slug}")
                            .check(css("#ProductDescription").isEL("#{description}")));

    public static final ChainBuilder addProductToCart =
            exec(view)
                    .exec(http("Add to Cart - ${name}")
                            .get("/cart/add/${id}")
                            .check(substring("items in your cart")))
                    .exec(session -> {
                        double currentCartTotal = session.getDouble("cartTotal");
                        double itemPrice = session.getDouble("price");
                        return session.set("cartTotal", currentCartTotal + itemPrice);
                    })
                    .exec(
                            session -> {
                                System.out.println("Cart Total : " + session.get("cartTotal").toString());
                                return session;
                            }
                    );
}
