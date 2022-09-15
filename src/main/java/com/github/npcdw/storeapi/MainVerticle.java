package com.github.npcdw.storeapi;

import com.github.npcdw.storeapi.config.SqliteConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // Init Sqlite
    SqliteConfig.init(vertx);

    // Create a Router
    Router router = Router.router(vertx);

    // Mount the handler for all incoming requests at every path and HTTP method
    router.route().handler(context -> {
      // Get the address of the request
      String address = context.request().connection().remoteAddress().toString();
      // Get the query parameter "name"
      MultiMap queryParams = context.queryParams();
      String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";
      // Write a json response
      context.json(
        new JsonObject()
          .put("name", name)
          .put("address", address)
          .put("message", "Hello " + name + " connected from " + address)
      );
    });

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
