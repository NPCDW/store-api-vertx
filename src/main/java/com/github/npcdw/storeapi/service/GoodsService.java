package com.github.npcdw.storeapi.service;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class GoodsService {

    public void list(RoutingContext context) {

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
    }

}
