package com.github.npcdw.storeapi.config;

import com.github.npcdw.storeapi.entity.consts.GlobalConst;
import com.github.npcdw.storeapi.service.GoodsService;
import com.github.npcdw.storeapi.util.SnowFlakeUtil;
import io.reactiverse.contextual.logging.ContextualData;
import io.vertx.core.Vertx;
import io.vertx.core.impl.ContextInternal;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.MDC;

import java.awt.image.renderable.ContextualRenderedImageFactory;

public class RouterConfig {

    public static Router init(Vertx vertx) {
        Router router = Router.router(vertx);
        router.route().handler(ctx -> {
            long traceId = SnowFlakeUtil.nextId();
            ContextualData.put("traceId", traceId + "");
            ctx.next();
        });

        GoodsService goodsService = new GoodsService();
        String prefix = GlobalConst.GLOBAL_API_PREFIX + "/goods";
        // Mount the handler for all incoming requests at every path and HTTP method
        router.get(prefix + "/list").handler(goodsService::list);
        router.get(prefix + "/getInfo/:id").handler(goodsService::getInfo);
        router.get(prefix + "/getInfoByQRCode").handler(goodsService::getInfoByQRCode);
        router.post(prefix + "/create").handler(BodyHandler.create()).handler(goodsService::create);
        router.put(prefix + "/update").handler(BodyHandler.create()).handler(goodsService::update);
        router.delete(prefix + "/remove/:id").handler(goodsService::remove);
        return router;
    }

}
