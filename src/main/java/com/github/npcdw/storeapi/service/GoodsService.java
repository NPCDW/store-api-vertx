package com.github.npcdw.storeapi.service;

import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.entity.common.ResponseResult;
import com.github.npcdw.storeapi.entity.common.TableInfo;
import com.github.npcdw.storeapi.mapper.GoodsMapper;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;

import java.util.ArrayList;

public class GoodsService {
    private final GoodsMapper goodsMapper = new GoodsMapper();

    public void list(RoutingContext context) {
        MultiMap queryParams = context.queryParams();
        int pageNumber = queryParams.contains("pageNumber") ? Integer.parseInt(queryParams.get("pageNumber")) : 0;
        int pageSize = queryParams.contains("pageSize") ? Integer.parseInt(queryParams.get("pageSize")) : 0;
        String name = queryParams.get("name");

        goodsMapper.count(name)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("获取失败"));
                })
                .onSuccess(count -> {
                    System.out.println(count);
                    if (count <= 0) {
                        context.json(ResponseResult.ok(new TableInfo<Goods>(0, new ArrayList<>())));
                        return;
                    }
                    goodsMapper.list(pageNumber, pageSize, name)
                            .onFailure(e -> {
                                e.printStackTrace();
                                context.json(ResponseResult.error("获取失败"));
                            })
                            .onSuccess(rows -> {
                                System.out.println(rows.size() + " " + rows.rowCount());
                                context.json(ResponseResult.ok(new TableInfo<>(count, rows)));});
                });
    }

    public void getInfo(RoutingContext context) {
        int id = Integer.parseInt(context.pathParam("id"));

        goodsMapper.getById(id)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("获取失败"));
                })
                .onSuccess(info -> {
                    context.json(ResponseResult.ok(info));
                });
    }

    public void getInfoByQRCode(RoutingContext context) {
        String qrcode = context.queryParams().get("qrcode");

        goodsMapper.getByQRCode(qrcode)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("获取失败"));
                })
                .onSuccess(info -> {
                    context.json(ResponseResult.ok(info));
                });
    }

    public void create(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);

        goodsMapper.insert(goods)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("添加失败"));
                })
                .onSuccess(result -> {
                    if (result.rowCount() > 0) {
                        long id = result.property(JDBCPool.GENERATED_KEYS).getLong(0);
                        context.json(ResponseResult.ok(id));
                    } else {
                        context.json(ResponseResult.error("添加失败"));
                    }
                });
    }

    public void update(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);

        goodsMapper.update(goods)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("更新失败"));
                })
                .onSuccess(result -> {
                    if (result.rowCount() > 0) {
                        context.json(ResponseResult.ok());
                    } else {
                        context.json(ResponseResult.error("更新失败"));
                    }
                });
    }

    public void remove(RoutingContext context) {
        int id = Integer.parseInt(context.pathParam("id"));

        goodsMapper.delete(id)
                .onFailure(e -> {
                    e.printStackTrace();
                    context.json(ResponseResult.error("删除失败"));
                })
                .onSuccess(result -> {
                    if (result.rowCount() > 0) {
                        context.json(ResponseResult.ok());
                    } else {
                        context.json(ResponseResult.error("数据不存在"));
                    }
                });
    }

}
