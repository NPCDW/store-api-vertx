package com.github.npcdw.storeapi.config;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SqliteConfig {
    public static JDBCPool pool;

    public static void init(Vertx vertx) throws Exception {
        String sqliteFilePath = "/data/sqlite/db/storeapi.db";

        initSqliteFile(sqliteFilePath);

        pool = initJDBCPool(vertx, sqliteFilePath);

        initDB(pool);
    }

    private static void initDB(JDBCPool pool) {
        //判断数据表是否存在
        pool.query("SELECT * FROM goods").execute()
            .onSuccess(rows -> {
                System.out.println("DB is normal");
            })
            .onFailure(ex -> {
                System.out.println("DB isn't exist, start init db");
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
                    e.printStackTrace();
                }
                if (str == null || str.length() == 0) {
                    System.out.println("read init.sql fail");
                    return;
                }
                //分割并执行sql
                String[] sqls = str.split(";");
                Future<RowSet<Row>> future = Future.succeededFuture();
                for (String sql : sqls) {
                    if (StringUtils.isBlank(sql)) {
                        continue;
                    }
                    future = future.compose(res -> pool.preparedQuery(sql).execute(), e -> pool.preparedQuery(sql).execute());
                    future.onFailure(Throwable::printStackTrace);
                }
                future.onComplete(res -> System.out.println("finish init db"));
            });
    }

    private static JDBCPool initJDBCPool(Vertx vertx, String filePath) throws Exception {
        return JDBCPool.pool(
            vertx,
            // configure the connection
            new JDBCConnectOptions()
                // H2 connection string
                .setJdbcUrl("jdbc:sqlite:" + filePath),
            // configure the pool
            new PoolOptions()
                .setMaxSize(16)
        );
    }

    private static void initSqliteFile(String filePath) {
        File file = new File(filePath);

        File dir = file.getParentFile();
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }

        if (!file.exists()) {
            try {
                boolean create = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
