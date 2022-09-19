package com.github.npcdw.storeapi.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class StoreConfig {
    public static ConfigRetriever init(Vertx vertx) {
        // resource 下的 config.json
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setConfig(new JsonObject().put("path", "config.json"));
        // 启动目录下的 config.json
        ConfigStoreOptions fileStore2 = new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setConfig(new JsonObject().put("path", "./config/config.json"));
        ConfigStoreOptions envPropsStore = new ConfigStoreOptions().setType("env")
            .setOptional(true)
            .setConfig(new JsonObject().put("keys", new JsonArray().add("token")).put("raw-data", true));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore).addStore(fileStore2).addStore(envPropsStore);

        return ConfigRetriever.create(vertx, options);
    }
}
