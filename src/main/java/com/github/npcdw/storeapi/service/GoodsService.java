package com.github.npcdw.storeapi.service;

import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.entity.common.ResponseResult;
import com.github.npcdw.storeapi.entity.common.TableInfo;
import com.github.npcdw.storeapi.mapper.GoodsMapper;
import com.github.npcdw.storeapi.util.SnowFlakeUtil;
import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class GoodsService {
    private static final Logger log = LoggerFactory.getLogger(GoodsService.class);
    private final GoodsMapper goodsMapper = new GoodsMapper();

    public void list(RoutingContext context) {
        log.info("1");
        MultiMap queryParams = context.queryParams();
        int pageNumber = queryParams.contains("pageNumber") ? Integer.parseInt(queryParams.get("pageNumber")) : 0;
        int pageSize = queryParams.contains("pageSize") ? Integer.parseInt(queryParams.get("pageSize")) : 0;
        String name = queryParams.get("name");

        goodsMapper.count(name)
                .onFailure(e -> {
                    log.error("Goods count fail", e);
                    context.json(ResponseResult.error("获取失败"));
                })
                .onSuccess(count -> {
                    log.info("4");
                    if (count <= 0) {
                        context.json(ResponseResult.ok(new TableInfo<Goods>(0, new ArrayList<>())));
                        return;
                    }
                    goodsMapper.list(pageNumber, pageSize, name)
                            .onFailure(e -> {
                                log.error("Goods list fail", e);
                                context.json(ResponseResult.error("获取失败"));
                            })
                            .onSuccess(list -> {
                                log.info("7");
                                context.json(ResponseResult.ok(new TableInfo<>(count, list)));
                            });
                });
    }

    public void getInfo(RoutingContext context) {
        int id = Integer.parseInt(context.pathParam("id"));

        goodsMapper.getById(id)
                .onFailure(e -> {
                    log.error("Goods getById fail", e);
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
                    log.error("Goods getByQRCode fail", e);
                    context.json(ResponseResult.error("获取失败"));
                })
                .onSuccess(info -> {
                    context.json(ResponseResult.ok(info));
                });
    }

    public void create(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);
        if (StringUtils.isBlank(goods.getQrcode())) {
            goods.setQrcode(-SnowFlakeUtil.nextId() + "");
        }

        goodsMapper.insert(goods)
                .onFailure(e -> {
                    log.error("Goods insert fail", e);
                    context.json(ResponseResult.error("添加失败"));
                })
                .onSuccess(result -> {
                    if (result > 0) {
                        context.json(ResponseResult.ok(goods.getId()));
                    } else {
                        context.json(ResponseResult.error("添加失败"));
                    }
                });
    }

    public void update(RoutingContext context) {
        Goods goods = context.body().asPojo(Goods.class);

        goodsMapper.update(goods)
                .onFailure(e -> {
                    log.error("Goods update fail", e);
                    context.json(ResponseResult.error("更新失败"));
                })
                .onSuccess(result -> {
                    if (result > 0) {
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
                    log.error("Goods delete fail", e);
                    context.json(ResponseResult.error("删除失败"));
                })
                .onSuccess(result -> {
                    if (result > 0) {
                        context.json(ResponseResult.ok());
                    } else {
                        context.json(ResponseResult.error("数据不存在"));
                    }
                });
    }

}
