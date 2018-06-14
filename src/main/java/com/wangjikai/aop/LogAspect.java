package com.wangjikai.aop;

import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author wang.
 * @date 2018/5/24.
 * Description: 基于aop模式的aspect切面日志管理
 */
@Component
@Aspect
public class LogAspect {

    @Pointcut("execution(* com.wangjikai..*.aa(..))")
    public void aaaa() {}

    @Before("aaaa()")
    public void before() {
        System.out.println("切面植入前");
    }

    @After("aaaa()")
    public void after() {
        System.out.println("切面植入后");
    }

}
