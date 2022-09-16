package com.github.npcdw.storeapi.config;

import com.github.npcdw.storeapi.entity.consts.GlobalConst;
import com.github.npcdw.storeapi.service.GoodsService;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class RouterConfig {

    public static Router init(Vertx vertx) {
        Router router = Router.router(vertx);

        GoodsService goodsService = new GoodsService();
        String prefix = GlobalConst.GLOBAL_API_PREFIX + "/goods";
        // Mount the handler for all incoming requests at every path and HTTP method
        router.get(prefix + "/list").handler(goodsService::list);
        router.get(prefix + "/getInfo/:id").handler(goodsService::getInfo);
        router.get(prefix + "/getInfoByQRCode").handler(goodsService::getInfoByQRCode);
        router.post(prefix + "/create").handler(goodsService::create);
        router.put(prefix + "/update").handler(goodsService::update);
        router.delete(prefix + "/remove/:id").handler(goodsService::remove);
        return router;
    }

}
