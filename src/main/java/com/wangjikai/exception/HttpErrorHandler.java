package com.wangjikai.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wang.
 * @date 2018/6/11.
 * Description:
 */
@Controller
public class HttpErrorHandler implements ErrorController {
    private final static String ERROR_PATH = "/error";

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * 返回页面错误
     */
    @RequestMapping(value = ERROR_PATH, produces = "text/html")
    public String errorHtml() {
        return "notfound";
    }

    /**
     * json错误返回
     */
    @RequestMapping(value = ERROR_PATH)
    @ResponseBody
    public Object error() {
        return "404";
    }
}
