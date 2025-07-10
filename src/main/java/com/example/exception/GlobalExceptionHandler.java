package com.example.exception;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * <p>
 * 使用 {@link ControllerAdvice} 注解，使其能够捕获指定包（com.example.controller）下
 * 所有 Controller 抛出的异常，并进行统一处理。
 * </p>
 */
@ControllerAdvice(basePackages="com.example.controller")
public class GlobalExceptionHandler {

    private static final Log log = LogFactory.get();


    /**
     * 统一处理所有未被特定 @ExceptionHandler 捕获的 {@link Exception}。
     * @param request HTTP请求对象
     * @param e       捕获到的异常
     * @return        一个表示系统异常的通用 Result 对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<?> error(HttpServletRequest request, Exception e){
        log.error("异常信息：",e);
        return Result.error("-1", "系统异常");
    }

    /**
     * 专门处理自定义的 {@link CustomException}。
     * <p>
     * 这样做的好处是，可以从自定义异常中提取出明确的错误码和错误信息，
     * 并将其返回给前端，而不是返回一个通用的“系统异常”消息。
     * </p>
     * @param request HTTP请求对象
     * @param e       捕获到的自定义异常
     * @return        一个包含具体错误码和信息的 Result 对象
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Result<?> customError(HttpServletRequest request, CustomException e){
        return Result.error(e.getCode(), e.getMsg());
    }
}
