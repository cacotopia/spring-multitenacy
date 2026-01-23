package com.ascude.multitenancy.demo.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ascude.multitenancy.demo.util.ExcepttionHandlerUtils;
import com.ascude.multitenancy.demo.util.ExcepttionHandlerUtils.ExceptionStrategy;
import com.ascude.multitenancy.demo.util.Result;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志记录器，用于记录异常信息。
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 异常处理策略映射，使用Map存储异常类型及其对应的处理策略。
     */
    private final Map<Class<? extends Exception>, ExceptionStrategy> exceptionStrategyMap;

    /**
     * 构造器，初始化异常处理策略映射，并调用initExceptionHandlers方法来填充异常处理策略。
     */
    public GlobalExceptionHandler() {
        exceptionStrategyMap = ExcepttionHandlerUtils.initExceptionHandlers();
    }

    /**
     * 统一处理所有的异常，根据异常类型调用相应的处理策略。
     *
     * @param request 当前HTTP请求对象
     * @param e       抛出的异常对象
     * @return 返回错误视图或null（对于Ajax请求）
     */
    @ExceptionHandler(Exception.class)
    public Result handleAllException(HttpServletRequest request, Exception e) {

        // 获取对应的异常处理器
        ExceptionStrategy handler = exceptionStrategyMap.entrySet().stream()
                .filter(entry -> entry.getKey().isInstance(e)) // 过滤出匹配的异常类型
                .findFirst() // 获取第一个匹配项
                .map(Map.Entry::getValue) // 获取处理策略
                .orElse((ex, req) -> { // 如果没有匹配的处理策略，则使用默认的处理策略
                    log.error("未知异常: {}, URL: {}", ex.getMessage(), req.getRequestURL()); // 记录未知错误信息
                    return MyException.builder()
                            .msg("系统发生未知错误") // 设置错误消息为系统发生未知错误
                            .errorType(ex.getClass().getSimpleName()) // 设置错误类型
                            .build();
                });

        // 处理异常，得到MyException对象
        MyException errorInfo = handler.handle(e, request);

        // 处理Ajax请求
//        if (ToolUtil.isJson(request)) {
//
//        }else{
//            return Result.error(ResultCode.BAD_PARAM, MessageConstants.System.SYSTEM_ERROR_VIEW);
//        }
        return Result.error(errorInfo.getCode(), errorInfo.getMessage());
    }
}