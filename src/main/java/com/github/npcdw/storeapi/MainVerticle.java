package com.github.npcdw.storeapi;

import com.github.npcdw.storeapi.config.RouterConfig;
import com.github.npcdw.storeapi.config.SqliteConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        // Init Sqlite
        SqliteConfig.init(vertx);

        // Create a Router
        Router router = RouterConfig.init(vertx);;

        vertx.createHttpServer()
            // Handle every request using the router
            .requestHandler(router)
            .listen(8888, http -> {
                if (http.succeeded()) {
                    startPromise.complete();
                    System.out.println("HTTP server started on port 8888");
                } else {
                    startPromise.fail(http.cause());
                }
            });
    }
}
