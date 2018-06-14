package com.wangjikai.pattern;

/**
 * @author wang.
 * @date 2018/5/31.
 * Description: 单例模式singleton，懒汉式，
 */
public class Singleton2 {
    /**
     * 构造方法私有化，不允许外部直接创建对象
     */
    private Singleton2() {}

    /**
     * 【声明】类的唯一实例
     */
    private static Singleton2 instance;

    /**
     * 获取实例的方法
     */
    public static Singleton2 getInstance() {
        if (instance == null) {
            System.out.print("创建新的实例");
            instance = new Singleton2();
        }
        return instance;
    }
}
class Outter2 {
    public static void main(String[] args) {

//        Singleton2 s1 = Singleton2.getInstance();
//        Singleton2 s2 = Singleton2.getInstance();
//        System.out.println(s1);
//        System.out.println(s2);




    }
}
