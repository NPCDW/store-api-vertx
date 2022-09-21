package com.github.npcdw.storeapi.config;

import com.github.npcdw.storeapi.entity.GlobalConfig;
import com.github.npcdw.storeapi.util.FileUtil;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.CopyOptions;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqliteConfig {
    private static final Logger log = LoggerFactory.getLogger(SqliteConfig.class);
    private static final List<String> versionList = new ArrayList<String>() {{
        add("2022-09-20-00");
        add("2022-09-21-00");
    }};
    public static JDBCPool pool;

    public static Future<Void> init(Vertx vertx) {
        String sqliteFilePath = GlobalConfig.INSTANCE.getSqlite().getFilepath();
        try {
            initSqliteFile(sqliteFilePath);
        } catch (Exception e) {
            return Future.failedFuture(e);
        }
        pool = initJDBCPool(vertx, sqliteFilePath);

        return pool.query("SELECT version FROM version order by id desc limit 1").execute()
            .compose(rows -> upgradeDB(vertx, pool, rows, sqliteFilePath), ex -> initDB(pool));
    }

    private static Future<Void> initDB(JDBCPool pool) {
        log.info("DB isn't exist, start init db");
        //获取初始化sql
        String str = null;
        try {
            str = FileUtil.readResourceAsString("sqlite/init.sql");
        } catch (Exception e) {
            log.error("read init.sql fail");
            return Future.failedFuture(e);
        }
        if (str == null || str.length() == 0) {
            log.error("read init.sql fail");
            return Future.failedFuture("read init.sql fail");
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
    }

    private static Future<Void> upgradeDB(Vertx vertx, JDBCPool pool, RowSet<Row> rows, String sqliteFilePath) {
        log.info("DB is exist");
        String currentVersion = rows.iterator().next().getString(0);
        int index = versionList.indexOf(currentVersion);
        if (index == versionList.size() - 1) {
            log.info("DB version is matched, Current version is {}", currentVersion);
            return Future.succeededFuture();
        }
        log.info("DB version require {}, Current version is {}, start upgrade db", versionList.get(versionList.size() - 1), currentVersion);
        File file = new File(sqliteFilePath);
        return vertx.fileSystem()
            .copy(sqliteFilePath, file.getParentFile() + "/storeapi-" + currentVersion + ".db", new CopyOptions().setReplaceExisting(true))
            .compose(r -> {
                Future<RowSet<Row>> future = Future.succeededFuture();
                for (int i = index + 1; i < versionList.size(); i++) {
                    String version = versionList.get(i);
                    String str = null;
                    try {
                        str = FileUtil.readResourceAsString("sqlite/upgrade/" + version + ".sql");
                    } catch (Exception e) {
                        log.error("read {}.sql fail", version, e);
                        return Future.failedFuture(e);
                    }
                    if (str == null || str.length() == 0) {
                        log.error("read {}.sql fail", version);
                        return Future.failedFuture("read " + version + ".sql fail");
                    }
                    //分割并执行sql
                    String[] sqls = str.split(";");
                    for (String sql : sqls) {
                        if (StringUtils.isBlank(sql)) {
                            continue;
                        }
                        future = future.compose(res -> pool.preparedQuery(sql).execute());
                    }
                }
                return future.compose(res -> {
                    log.info("finish upgrade db");
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
                .setMaxSize(GlobalConfig.INSTANCE.getSqlite().getMaxPoolSize())
                .setShared(true)
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
