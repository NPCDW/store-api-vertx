# Store-api-vertx

!["https://vertx.io"](https://img.shields.io/badge/vert.x-4.3.3-purple.svg)
!["https://www.sqlite.org"](https://img.shields.io/badge/sqlite-3.39.3-blue.svg)

## 简介

### 主要功能

`vertx4 + sqlite3`，使用 `vertx-web` 实现 `http` 服务器，以及对 `sqlite` 进行初始化，建表，数据的增删改查。

### 尚未实现

程序升级时如何对已存在的 `sqlite` 数据库进行升级，尚未对接日志组件

### 使用目的

`springboot + mysql` 内存占用太夸张了，启动内存就超过了 `650M`，其中 `springboot` 启动时 `256M`， `mysql` 启动时超 `400M`，

而 `vertx + sqlite` 启动 `70M`，如果接口调用量没有特别大，`128M` 的内存应该能压住。

原 `springboot` 项目以及其他 `JavaWeb` 框架推荐：[https://github.com/NPCDW/store-api](https://github.com/NPCDW/store-api)

## Official

This application was generated using http://start.vertx.io

## Building

To run your application:
```shell
mvn clean compile exec:java
```

To package your application:
```shell
mvn clean package
```

## Help

* [Vert.x Documentation](https://vertx.io/docs/)
* [Vert.x Stack Overflow](https://stackoverflow.com/questions/tagged/vert.x?sort=newest&pageSize=15)
* [Vert.x User Group](https://groups.google.com/forum/?fromgroups#!forum/vertx)
* [Vert.x Gitter](https://gitter.im/eclipse-vertx/vertx-users)

