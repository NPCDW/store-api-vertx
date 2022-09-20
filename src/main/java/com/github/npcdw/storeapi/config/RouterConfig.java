package com.github.npcdw.storeapi.config;

import com.github.npcdw.storeapi.entity.GlobalConfig;
import com.github.npcdw.storeapi.entity.common.ResponseResult;
import com.github.npcdw.storeapi.entity.consts.GlobalConst;
import com.github.npcdw.storeapi.service.GoodsService;
import com.github.npcdw.storeapi.util.SnowFlakeUtil;
import io.reactiverse.contextual.logging.ContextualData;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouterConfig {
    private static final Logger log = LoggerFactory.getLogger(RouterConfig.class);

    public static Router init(Vertx vertx) {
        Router router = Router.router(vertx);
        router.route().handler(ctx -> {
            String token = ctx.request().getHeader("token");
            if (token == null || !token.equals(GlobalConfig.INSTANCE.getToken())) {
                ctx.json(ResponseResult.error("token校验失败"));
            } else {
                ctx.next();
            }
        }).handler(ctx -> {
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

        router.route().failureHandler(ctx -> {
            log.error("路由：{}，请求体：{}，异常：", ctx.request().uri(), ctx.body().asString(), ctx.failure());
            ctx.json(ResponseResult.error("INTERNAL SERVER ERROR"));
        });
        return router;
    }

}
