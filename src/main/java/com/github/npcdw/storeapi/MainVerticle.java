package com.github.npcdw.storeapi;

import com.github.npcdw.storeapi.config.RouterConfig;
import com.github.npcdw.storeapi.config.SqliteConfig;
import com.github.npcdw.storeapi.config.StoreConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(MainVerticle.class);

    @Override
    public void start(Promise<Void> startPromise) {
        // Get Config
        StoreConfig.init(vertx).getConfig(result -> {
            StoreConfig.config = result.result();

            // Init Sqlite
            SqliteConfig.init(vertx)
                .onFailure(e -> log.error("", e))
                .onComplete(r -> {
                    // Create a Router
                    Router router = RouterConfig.init(vertx);

                    vertx.createHttpServer()
                        // Handle every request using the router
                        .requestHandler(router)
                        .listen(8888, http -> {
                            if (http.succeeded()) {
                                startPromise.complete();
                                log.info("HTTP server started on port 8888");
                            } else {
                                startPromise.fail(http.cause());
                            }
                        });
                });
        });
    }
}
