package com.github.npcdw.storeapi.mapper;

import com.github.npcdw.storeapi.config.SqliteConfig;
import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.util.DateTimeUtil;
import io.vertx.core.Future;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.templates.TupleMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GoodsMapper {
    private static final Logger log = LoggerFactory.getLogger(GoodsMapper.class);

    private static final String fields = "id, create_time, update_time, qrcode, name, cover, price";

    TupleMapper<Goods> PARAMETERS_GOODS_MAPPER = TupleMapper.mapper(user -> {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", user.getId());
        parameters.put("create_time", DateTimeUtil.formatDateTime(user.getCreateTime()));
        parameters.put("update_time", DateTimeUtil.formatDateTime(user.getUpdateTime()));
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

    public Future<Goods> getById(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        String template = "SELECT " + fields +
            " FROM goods" +
            " WHERE id = #{id}";

        return SqlTemplate
            .forQuery(SqliteConfig.pool, template)
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters)
            .compose(rows -> {
                if (rows.size() > 1) {
                    return Future.failedFuture("required a single bean, but more were found");
                }
                for (Goods row : rows) {
                    return Future.succeededFuture(row);
                }
                return Future.succeededFuture(null);
            });
    }

    public Future<Goods> getByQRCode(String qrcode) {
        Map<String, Object> parameters = Collections.singletonMap("qrcode", qrcode);

        String template = "SELECT " + fields +
            " FROM goods" +
            " WHERE qrcode = #{qrcode}";

        return SqlTemplate
            .forQuery(SqliteConfig.pool, template)
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters)
            .compose(rows -> {
                if (rows.size() > 1) {
                    return Future.failedFuture("required a single bean, but more were found");
                }
                for (Goods row : rows) {
                    return Future.succeededFuture(row);
                }
                return Future.succeededFuture(null);
            });
    }

    public Future<Integer> count(String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        String template = "SELECT count(*)" +
            " FROM goods" +
            (StringUtils.isBlank(name) ? "" : " WHERE name like '%'||#{name}||'%'");

        return SqlTemplate
            .forQuery(SqliteConfig.pool, template)
            .execute(parameters)
            .compose(rows -> {
                for (Row row : rows) {
                    return Future.succeededFuture(row.getInteger(0));
                }
                return Future.failedFuture("未查询到任何内容");
            });
    }

    public Future<List<Goods>> list(int pageNumber, int pageSize, String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("start", (pageNumber - 1) * pageSize);
        parameters.put("pageSize", pageSize);

        String template = "SELECT " + fields +
            " FROM goods" +
            (StringUtils.isBlank(name) ? "" : " WHERE name like '%'||#{name}||'%'") +
            " LIMIT #{pageSize} OFFSET #{start}";

        return SqlTemplate
            .forQuery(SqliteConfig.pool, template)
            .mapTo(ROW_GOODS_MAPPER)
            .execute(parameters)
            .compose(rows -> {
                List<Goods> list = new ArrayList<>();
                for (Goods row : rows) {
                    list.add(row);
                }
                return Future.succeededFuture(list);
            });
    }

    public Future<Integer> insert(Goods record) {
        String template = "insert into goods (qrcode, name, cover, price)" +
            " values (#{qrcode}, #{name}, #{cover}, #{price})";

        return SqlTemplate
            .forUpdate(SqliteConfig.pool, template)
            .mapFrom(PARAMETERS_GOODS_MAPPER)
            .execute(record)
            .compose(result -> {
                int id = result.property(JDBCPool.GENERATED_KEYS).getInteger(0);
                record.setId(id);
                return Future.succeededFuture(result.rowCount());
            });
    }

    public Future<Integer> update(Goods record) {
        record.setUpdateTime(new Date());

        String template = "update goods set" +
            "   update_time = #{update_time}," +
            (StringUtils.isBlank(record.getQrcode()) ? "" : "   qrcode = #{qrcode},") +
            (StringUtils.isBlank(record.getName()) ? "" : "   name = #{name},") +
            (StringUtils.isBlank(record.getCover()) ? "" : "   cover = #{cover},") +
            (record.getPrice() == null ? "" : "   price = #{price},") +
            " where id = #{id}";
        StringBuilder str = new StringBuilder(template);
        str.deleteCharAt(template.lastIndexOf(","));
        template = str.toString();

        return SqlTemplate
            .forUpdate(SqliteConfig.pool, template)
            .mapFrom(PARAMETERS_GOODS_MAPPER)
            .execute(record)
            .compose(result -> Future.succeededFuture(result.rowCount()));
    }

    public Future<Integer> delete(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        String template = "delete from goods" +
            " where id = #{id}";

        return SqlTemplate
            .forUpdate(SqliteConfig.pool, template)
            .execute(parameters)
            .compose(result -> Future.succeededFuture(result.rowCount()));
    }

}
