package com.github.npcdw.storeapi.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SqliteConfig {
    private static final Logger log = LoggerFactory.getLogger(SqliteConfig.class);
    public static JDBCPool pool;

    public static Future<Void> init(Vertx vertx) {
        String sqliteFilePath = "/data/sqlite/db/storeapi.db";
        try {
            initSqliteFile(sqliteFilePath);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        pool = initJDBCPool(vertx, sqliteFilePath);

        return initDB(pool);
    }

    private static Future<Void> initDB(JDBCPool pool) {
        //判断数据表是否存在
        return pool.query("SELECT count(*) FROM goods").execute()
            .compose(rows -> {
                log.info("DB is normal");
                return Future.succeededFuture();
            }, ex -> {
                log.info("DB isn't exist, start init db");
                //获取初始化sql
                String str = null;
                try (InputStream input = SqliteConfig.class.getClassLoader().getResourceAsStream("init.sql");
                     ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) != -1) {
                        output.write(buffer, 0, length);
                    }
                    str = output.toString(StandardCharsets.UTF_8.name());
                } catch (IOException e) {
                    log.error("获取初始化sql失败", e);
                    return Future.failedFuture(e);
                }
                if (str == null || str.length() == 0) {
                    log.error("read init.sql fail");
                    return Future.failedFuture(new Exception(""));
                }
                //分割并执行sql
                String[] sqls = str.split(";");
                Future<RowSet<Row>> future = Future.succeededFuture();
                for (String sql : sqls) {
                    if (StringUtils.isBlank(sql)) {
                        continue;
                    }
                    future = future.compose(res -> pool.preparedQuery(sql).execute());
                }
                return future.compose(res -> {
                    log.info("finish init db");
                    return Future.succeededFuture();
                }, Future::failedFuture);
            });
    }

    private static JDBCPool initJDBCPool(Vertx vertx, String filePath) {
        return JDBCPool.pool(
            vertx,
            // configure the connection
            new JDBCConnectOptions()
                // H2 connection string
                .setJdbcUrl("jdbc:sqlite:" + filePath),
            // configure the pool
            new PoolOptions()
                .setMaxSize(5)
        );
    }

    private static void initSqliteFile(String filePath) throws IOException {
        File file = new File(filePath);

        File dir = file.getParentFile();
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }

        if (!file.exists()) {
            try {
                boolean create = file.createNewFile();
            } catch (IOException e) {
                log.error("创建sqlite文件失败", e);
                throw e;
            }
        }
    }

}
