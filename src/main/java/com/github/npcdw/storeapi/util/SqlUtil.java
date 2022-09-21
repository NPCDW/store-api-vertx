package com.github.npcdw.storeapi.util;

import com.github.npcdw.storeapi.config.SqliteConfig;
import io.vertx.core.Future;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import io.vertx.sqlclient.templates.TupleMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlUtil {

    public static <T> Future<T> getOne(String sqlTemplate, Map<String, Object> parameters, RowMapper<T> rowMapper) {
        return SqlTemplate
            .forQuery(SqliteConfig.pool, sqlTemplate)
            .mapTo(rowMapper)
            .execute(parameters)
            .compose(rows -> {
                if (rows.size() > 1) {
                    return Future.failedFuture("required a single bean, but more were found");
                }
                if (rows.size() < 1) {
                    return Future.succeededFuture(null);
                }
                return Future.succeededFuture(rows.iterator().next());
            });
    }

    public static Future<Integer> count(String sqlTemplate, Map<String, Object> parameters) {
        return SqlTemplate
            .forQuery(SqliteConfig.pool, sqlTemplate)
            .execute(parameters)
            .compose(rows -> Future.succeededFuture(rows.iterator().next().getInteger(0)));
    }

    public static <T> Future<List<T>> list(String sqlTemplate, Map<String, Object> parameters, RowMapper<T> rowMapper) {
        return SqlTemplate
            .forQuery(SqliteConfig.pool, sqlTemplate)
            .mapTo(rowMapper)
            .execute(parameters)
            .compose(rows -> {
                List<T> list = new ArrayList<>();
                for (T row : rows) {
                    list.add(row);
                }
                return Future.succeededFuture(list);
            });
    }

    public static <T> Future<Integer> insert(String sqlTemplate, T t, TupleMapper<T> parametersMapper) {
        return SqlTemplate
            .forUpdate(SqliteConfig.pool, sqlTemplate)
            .mapFrom(parametersMapper)
            .execute(t)
            .compose(result -> {
                int id = result.property(JDBCPool.GENERATED_KEYS).getInteger(0);
                return Future.succeededFuture(id);
            });
    }

    public static <T> Future<Integer> exec(String sqlTemplate, T t, TupleMapper<T> parametersMapper) {
        return SqlTemplate
            .forUpdate(SqliteConfig.pool, sqlTemplate)
            .mapFrom(parametersMapper)
            .execute(t)
            .compose(result -> Future.succeededFuture(result.rowCount()));
    }

    public static <T> Future<Integer> exec(String sqlTemplate, Map<String, Object> parameters) {
        return SqlTemplate
            .forUpdate(SqliteConfig.pool, sqlTemplate)
            .execute(parameters)
            .compose(result -> Future.succeededFuture(result.rowCount()));
    }

}
