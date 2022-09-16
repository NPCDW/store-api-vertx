package com.github.npcdw.storeapi.entity.common;

/**
 * 返回结果包装类
 */
public class ResponseResult<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private T data;

    public static ResponseResult<Void> ok() {
        ResponseResult<Void> responseResult = new ResponseResult<>();
        responseResult.setSuccess(true);
        responseResult.setCode(20000);
        responseResult.setMessage("成功");
        return responseResult;
    }

    public static <T> ResponseResult<T> ok(T data) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setSuccess(true);
        responseResult.setCode(20000);
        responseResult.setMessage("成功");
        responseResult.setData(data);
        return responseResult;
    }

    public static <T> ResponseResult<T> error(String message) {
        ResponseResult<T> responseResult = new ResponseResult<>();
        responseResult.setSuccess(false);
        responseResult.setCode(50000);
        responseResult.setMessage(message);
        return responseResult;
    }

    public ResponseResult<T> message(String message) {
        this.setMessage(message);
        return this;
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
