package com.github.npcdw.storeapi.entity.common;

import io.vertx.core.json.JsonObject;

import java.io.IOException;

/**
 * 返回结果包装类
 */
public class ResponseResult<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;

    public static JsonObject ok() {
        JsonObject responseResult = new JsonObject();
        responseResult.put("success", true);
        responseResult.put("code", 20000);
        responseResult.put("message", "成功");
        responseResult.put("data", null);
        return responseResult;
    }

    public static <T> JsonObject ok(T data) {
        JsonObject responseResult = new JsonObject();
        responseResult.put("success", true);
        responseResult.put("code", 20000);
        responseResult.put("message", "成功");
        responseResult.put("data", data);
        return responseResult;
    }

    public static <T> JsonObject error(String message) {
        JsonObject responseResult = new JsonObject();
        responseResult.put("success", false);
        responseResult.put("code", 50000);
        responseResult.put("message", message);
        responseResult.put("data", null);
        return responseResult;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
