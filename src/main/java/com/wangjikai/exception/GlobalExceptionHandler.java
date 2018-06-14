package com.wangjikai.exception;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author wang.
 * @date 2018/6/11.
 * Description: 全局错误处理类
 */
@Controller
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler() {
        return "notfound";
    }
}
