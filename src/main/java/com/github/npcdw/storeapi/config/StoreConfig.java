package com.github.npcdw.storeapi.config;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class StoreConfig {
    public static JsonObject config;

    public static ConfigRetriever init(Vertx vertx) {
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
            .setType("file")
            .setConfig(new JsonObject().put("path", "config.json"));
        ConfigStoreOptions fileStore2 = new ConfigStoreOptions()
            .setType("file")
            .setOptional(true)
            .setConfig(new JsonObject().put("path", "./config.json"));
        ConfigStoreOptions sysPropsStore = new ConfigStoreOptions().setType("sys")
            .setOptional(true)
            .setConfig(new JsonObject().put("raw-data", true));;
        ConfigStoreOptions envPropsStore = new ConfigStoreOptions().setType("env")
            .setOptional(true)
            .setConfig(new JsonObject().put("raw-data", true));;

        ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore).addStore(fileStore2).addStore(envPropsStore).addStore(sysPropsStore);

        return ConfigRetriever.create(vertx, options);
    }

}
