package com.example.exception;

/**
 * 自定义业务异常类
 * <p>
 * 用于在业务逻辑中抛出预期的、可控的异常。
 * 继承自 {@link RuntimeException}，这是一个非受检异常，
 * 意味着调用者不需要强制性地用 try-catch 块来处理它。
 * </p>
 */
public class CustomException extends RuntimeException {
    /** 错误码，用于前端识别错误类型 */
    private String code;
    /** 错误信息，用于向用户展示 */
    private String msg;

    /**
     * 构造函数
     * @param code 错误码
     * @param msg  错误信息
     */
    public CustomException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

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
}
