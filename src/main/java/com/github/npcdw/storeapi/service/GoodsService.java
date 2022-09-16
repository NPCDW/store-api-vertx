package com.github.npcdw.storeapi.service;

import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.entity.common.ResponseResult;
import com.github.npcdw.storeapi.entity.common.TableInfo;
import com.github.npcdw.storeapi.mapper.GoodsMapper;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;

public class GoodsService {
    private final GoodsMapper goodsMapper = new GoodsMapper();

    public void list(RoutingContext context) {
        MultiMap queryParams = context.queryParams();
        int pageNumber = queryParams.contains("pageNumber") ? Integer.parseInt(queryParams.get("pageNumber")) : 0;
        int pageSize = queryParams.contains("pageSize") ? Integer.parseInt(queryParams.get("pageNumber")) : 0;
        String name = queryParams.get("name");

        goodsMapper.count(name)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(count -> {
                if (count <= 0) {
                    context.json(ResponseResult.ok(new TableInfo<Goods>(0, new ArrayList<>())));
                    return;
                }
                goodsMapper.list(pageNumber, pageSize, name)
                    .onFailure(e -> context.json(ResponseResult.error("获取失败")))
                    .onSuccess(rows -> context.json(ResponseResult.ok(new TableInfo<>(0, rows))));
            });
    }

    public void getInfo(RoutingContext context) {
        int id = Integer.parseInt(context.pathParam("id"));

        goodsMapper.getById(id)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(info -> {
                context.json(ResponseResult.ok(info));
            });
    }

    public void getInfoByQRCode(RoutingContext context) {
        String qrcode = context.pathParam("qrcode");

        goodsMapper.getByQRCode(qrcode)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(info -> {
                context.json(ResponseResult.ok(info));
            });
    }

    public void create(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);

        goodsMapper.insert(goods)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(result -> {
                context.json(ResponseResult.ok(result));
            });
    }

    public void update(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);

        goodsMapper.update(goods)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(result -> {
                context.json(ResponseResult.ok(result));
            });
    }

    public void remove(RoutingContext context) {
        int id = Integer.parseInt(context.pathParam("id"));

        goodsMapper.delete(id)
            .onFailure(e -> context.json(ResponseResult.error("获取失败")))
            .onSuccess(info -> {
                context.json(ResponseResult.ok(info));
            });
    }

}
