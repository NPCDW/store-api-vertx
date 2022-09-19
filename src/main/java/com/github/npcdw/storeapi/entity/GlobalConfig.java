package com.github.npcdw.storeapi.entity;

public class GlobalConfig {
    public static GlobalConfig INSTANCE;

    private Integer port;
    private String token;
    private Sqlite sqlite;

    public static class Sqlite {
        private String filepath;
        private Integer maxPoolSize;

        public String getFilepath() {
            return filepath;
        }

        public void setFilepath(String filepath) {
            this.filepath = filepath;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Sqlite getSqlite() {
        return sqlite;
    }

    public void setSqlite(Sqlite sqlite) {
        this.sqlite = sqlite;
    }

}
