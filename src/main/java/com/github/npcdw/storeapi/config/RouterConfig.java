package com.github.npcdw.storeapi.config;

import com.github.npcdw.storeapi.service.GoodsService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class RouterConfig {

    public static Router init(Vertx vertx) {
        Router router = Router.router(vertx);

        GoodsService goodsService = new GoodsService();
        // Mount the handler for all incoming requests at every path and HTTP method
        router.get("/goods/list").handler(goodsService::list);
        router.get("/goods/getInfo/:id").handler(goodsService::getInfo);
        router.get("/goods/getInfoByQRCode").handler(goodsService::getInfoByQRCode);
        router.post("/goods/create").handler(goodsService::create);
        router.put("/goods/update").handler(goodsService::update);
        router.delete("/goods/remove/:id").handler(goodsService::remove);
        return router;
    }

}
