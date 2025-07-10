package com.example.common;

/**
 * 通用返回结果类
 * 用于封装接口返回给前端的数据
 * @param <T> a T object.
 */
public class Result<T> {
    /** 状态码 */
    private String code;
    /** 提示信息 */
    private String msg;
    /** 数据对象 */
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 无参构造方法
     */
    public Result() {
    }

    /**
     * 带数据对象的构造方法
     * @param data 数据对象
     */
    public Result(T data) {
        this.data = data;
    }

    /**
     * 成功的静态方法，不返回数据
     * @return Result
     */
    public static Result success() {
        Result result = new Result<>();
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    /**
     * 成功的静态方法，返回数据
     * @param data 数据对象
     * @param <T> a T object.
     * @return Result
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>(data);
        result.setCode("0");
        result.setMsg("成功");
        return result;
    }

    /**
     * 失败的静态方法
     * @param code 状态码
     * @param msg 提示信息
     * @return Result
     */
    public static Result error(String code, String msg) {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
