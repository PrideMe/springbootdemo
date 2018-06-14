package com.wangjikai.pattern;

/**
 * @author wang.
 * @date 2018/5/31.
 * Description: 单例模式singleton，饿汉式，在类被加载时就已经确定
 * 有些对象只需要一个就足够了
 * 保证整个应用程序中某个实例有且仅有一个
 */
public class Singleton {

    /**
     * 构造方法私有化，不允许外部直接创建对象
     */
    private Singleton(){}

    /**
     * 创建类的唯一实例
     */
    private static Singleton instance = new Singleton();

    /**
     * 提供一个获取实例的方法
     */
    public static Singleton getInstance() {
        return instance;
    }

}
class Outter {
    public static void main(String[] args) {
        Singleton s1 = Singleton.getInstance();
        Singleton s2 = Singleton.getInstance();
        System.out.println(s1);
        System.out.println(s2);

        Singleton2 instance1 = Singleton2.getInstance();
        Singleton2 instance2 = Singleton2.getInstance();
        System.out.println(instance1);
        System.out.println(instance2);
    }
}