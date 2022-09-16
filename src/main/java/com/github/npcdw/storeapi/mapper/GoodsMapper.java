package com.github.npcdw.storeapi.mapper;

import com.github.npcdw.storeapi.config.SqliteConfig;
import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.util.DateTimeUtil;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.templates.TupleMapper;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GoodsMapper {
    TupleMapper<Goods> PARAMETERS_GOODS_MAPPER = TupleMapper.mapper(user -> {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", user.getId());
        parameters.put("create_time", user.getCreateTime());
        parameters.put("update_time", user.getUpdateTime());
        parameters.put("qrcode", user.getQrcode());
        parameters.put("name", user.getName());
        parameters.put("cover", user.getCover());
        parameters.put("price", user.getPrice());
        return parameters;
    });

    RowMapper<Goods> ROW_GOODS_MAPPER = row -> {
        Goods goods = new Goods();
        goods.setId(row.getInteger("id"));
        goods.setCreateTime(DateTimeUtil.parseDateTime(row.getString("create_time")));
        goods.setUpdateTime(DateTimeUtil.parseDateTime(row.getString("update_time")));
        goods.setQrcode(row.getString("qrcode"));
        goods.setName(row.getString("name"));
        goods.setCover(row.getString("cover"));
        goods.setPrice(row.getBigDecimal("price"));
        return goods;
    };


    public Future<Integer> count(String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        return SqlTemplate
            .forQuery(SqliteConfig.pool,
                "SELECT count(*)" +
                    " FROM goods" +
                    (StringUtils.isBlank(name) ? "" : " WHERE name like '%'||#{name}||'%'"))
            .execute(parameters)
            .compose(rows -> {
                for (Row row : rows) {
                    return Future.succeededFuture(row.getInteger(0));
                }
                return null;
            });
    }

    public Future<RowSet<Goods>> list(int pageNumber, int pageSize, String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("start", (pageNumber - 1) * pageSize);
        parameters.put("pageSize", pageSize);

        return SqlTemplate
            .forQuery(SqliteConfig.pool,
                "SELECT id, create_time, update_time, qrcode, name, cover, price" +
                    " FROM goods" +
                    (StringUtils.isBlank(name) ? "" : " WHERE name like '%'||#{name}||'%'") +
                    " LIMIT #{pageSize} OFFSET #{start}")
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters);
    }

    public Future<SqlResult<Void>> insert(Goods record) {
        return SqlTemplate
            .forUpdate(SqliteConfig.pool,
                "insert into goods (qrcode, name, cover, price)" +
                    " values (#{qrcode}, #{name}, #{cover}, #{price})")
            .mapFrom(PARAMETERS_GOODS_MAPPER)
            .execute(record);
    }

    public Future<SqlResult<Void>> update(Goods record) {
        record.setUpdateTime(new Date());
        return SqlTemplate
            .forUpdate(SqliteConfig.pool,
                "update goods set" +
                    "   update_time = #{update_time}," +
                    "   qrcode = #{qrcode}," +
                    "   name = #{name}," +
                    "   cover = #{cover}," +
                    "   price = #{price}" +
                    " where id = #{id}")
            .mapFrom(PARAMETERS_GOODS_MAPPER)
            .execute(record);
    }

    public Future<SqlResult<Void>> delete(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        return SqlTemplate
            .forUpdate(SqliteConfig.pool,
                "delete from goods" +
                    " where id = #{id}")
            .execute(parameters);
    }

    public Future<RowSet<Goods>> getById(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        return SqlTemplate
            .forQuery(SqliteConfig.pool,
                "SELECT id, create_time, update_time, qrcode, name, cover, price" +
                    " FROM goods" +
                    " WHERE id = #{id}")
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters);
    }

    public Future<RowSet<Goods>> getByQRCode(String qrcode) {
        Map<String, Object> parameters = Collections.singletonMap("qrcode", qrcode);

        return SqlTemplate
            .forQuery(SqliteConfig.pool,
                "SELECT id, create_time, update_time, qrcode, name, cover, price" +
                    " FROM goods" +
                    " WHERE qrcode = #{qrcode}")
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters);
    }

}
