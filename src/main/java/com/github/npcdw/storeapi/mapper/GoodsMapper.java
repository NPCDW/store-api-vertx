package com.github.npcdw.storeapi.mapper;

import com.github.npcdw.storeapi.config.SqliteConfig;
import com.github.npcdw.storeapi.entity.Goods;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

import java.util.Collections;
import java.util.Map;

public class GoodsMapper {

    public int count(String name) {
        Map<String, Object> parameters = Collections.singletonMap("id", 1);

        RowMapper<Goods> ROW_GOODS_MAPPER = row -> {
            Goods goods = new Goods();
            goods.setId(row.getInteger("id"));
            goods.setCreateTime(row.getLocalDateTime("create_time"));
            goods.setUpdateTime(row.getLocalDateTime("update_time"));
            goods.setQrcode(row.getString("qrcode"));
            goods.setName(row.getString("name"));
            goods.setCover(row.getString("cover"));
            goods.setPrice(row.getBigDecimal("price"));
            return goods;
        };

        SqlTemplate
            .forQuery(SqliteConfig.pool, "SELECT * FROM goods WHERE id=#{id}")
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters);
    }

}
