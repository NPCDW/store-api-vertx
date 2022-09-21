package com.github.npcdw.storeapi.mapper;

import com.github.npcdw.storeapi.entity.Goods;
import com.github.npcdw.storeapi.util.DateTimeUtil;
import com.github.npcdw.storeapi.util.SqlUtil;
import io.vertx.core.Future;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.TupleMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class GoodsMapper {
    private static final Logger log = LoggerFactory.getLogger(GoodsMapper.class);

    private static final String fields = "id, create_time, update_time, qrcode, name, cover, price, unit";

    TupleMapper<Goods> PARAMETERS_GOODS_MAPPER = TupleMapper.mapper(user -> {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", user.getId());
        parameters.put("create_time", DateTimeUtil.formatDateTime(user.getCreateTime()));
        parameters.put("update_time", DateTimeUtil.formatDateTime(user.getUpdateTime()));
        parameters.put("qrcode", user.getQrcode());
        parameters.put("name", user.getName());
        parameters.put("cover", user.getCover());
        parameters.put("price", user.getPrice());
        parameters.put("unit", user.getUnit());
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
        goods.setUnit(row.getString("unit"));
        return goods;
    };

    public Future<Goods> getById(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        String template = "SELECT " + fields +
            " FROM goods" +
            " WHERE id = #{id}";

        return SqlUtil.getOne(template, parameters, ROW_GOODS_MAPPER);
    }

    public Future<Goods> getByQRCode(String qrcode) {
        Map<String, Object> parameters = Collections.singletonMap("qrcode", qrcode);

        String template = "SELECT " + fields +
            " FROM goods" +
            " WHERE qrcode = #{qrcode}";

        return SqlUtil.getOne(template, parameters, ROW_GOODS_MAPPER);
    }

    public Future<Integer> count(String name) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", name);

        String template = "SELECT count(*)" +
            " FROM goods" +
            (StringUtils.isBlank(name) ? "" : " WHERE name like '%'||#{name}||'%'");

        return SqlUtil.count(template, parameters);
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

        return SqlUtil.list(template, parameters, ROW_GOODS_MAPPER);
    }

    public Future<Integer> insert(Goods record) {
        String template = "insert into goods (qrcode, name, cover, price, unit)" +
            " values (#{qrcode}, #{name}, #{cover}, #{price}, #{unit})";

        return SqlUtil.insert(template, record, PARAMETERS_GOODS_MAPPER);
    }

    public Future<Integer> update(Goods record) {
        record.setUpdateTime(new Date());

        String template = "update goods set" +
            "   update_time = #{update_time}," +
            (StringUtils.isBlank(record.getQrcode()) ? "" : "   qrcode = #{qrcode},") +
            (StringUtils.isBlank(record.getName()) ? "" : "   name = #{name},") +
            (StringUtils.isBlank(record.getCover()) ? "" : "   cover = #{cover},") +
            (record.getPrice() == null ? "" : "   price = #{price},") +
            (StringUtils.isBlank(record.getUnit()) ? "" : "   unit = #{unit},") +
            " where id = #{id}";
        StringBuilder str = new StringBuilder(template);
        str.deleteCharAt(template.lastIndexOf(","));
        template = str.toString();

        return SqlUtil.exec(template, record, PARAMETERS_GOODS_MAPPER);
    }

    public Future<Integer> delete(Integer id) {
        Map<String, Object> parameters = Collections.singletonMap("id", id);

        String template = "delete from goods" +
            " where id = #{id}";

        return SqlUtil.exec(template, parameters);
    }

}
