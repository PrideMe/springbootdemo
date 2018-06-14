package com.wangjikai.pattern;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;

/**
 * @author wang.
 * @date 2018/6/6.
 * Description:
 */
public class Car implements Moveable {
    @Override
    public void move() {
        System.out.println("运行中");
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
//继承方式实现代理
class Car2 extends Car {
    @Override
    public void move() {
        super.move();
    }
}
//通过聚合的方式，实现代理
class Car3 implements Moveable {
    private Moveable moveable;

    public Car3(Moveable moveable) {
        this.moveable = moveable;
    }

    @Override
    public void move() {
        long startTime =  System.currentTimeMillis();
        System.out.println("记录时间");
        moveable.move();
        long endTime =  System.currentTimeMillis();
        System.out.println("汽车行驶时间："+(endTime-startTime));
    }

//    public static void main(String[] args) {
//        Car car = new Car();
//        Moveable car4 = new Car4(car);
//        Moveable car3 = new Car3(car4);
//        car3.move();
//    }
}
class Car4 implements Moveable {
    private Moveable moveable;

    public Car4(Moveable moveable) {
        this.moveable = moveable;
    }

    @Override
    public void move() {
        System.out.println("日志记录开始");
        moveable.move();
        System.out.println("日志记录结束");
    }
}
class CarByJDKProxy implements InvocationHandler {

    private Object object;

    public CarByJDKProxy(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //System.out.println(proxy.getClass().getName());
        //System.out.println(object.getClass().getName());
        System.out.println("动态代理开始");
        method.invoke(object);
        System.out.println("动态代理结束");
        return proxy;
    }

    public static void main(String[] args) {
        Car car = new Car();
//        InvocationHandler invocationHandler = new CarByJDKProxy(car);
//        Class<?> cls = car.getClass();
//        Moveable moveable = (Moveable) Proxy.newProxyInstance(cls.getClassLoader(),cls.getInterfaces(),invocationHandler);
//        moveable.move();
        //System.out.println(moveable.getClass().getName());
    }
}