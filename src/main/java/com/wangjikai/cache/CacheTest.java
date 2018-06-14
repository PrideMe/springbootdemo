package com.wangjikai.cache;

import java.util.ArrayList;

/**
 * @author wang.
 * @date 2018/5/23.
 * Description:
 * redis : https://blog.csdn.net/u012943767/article/details/79360748
 */
public class CacheTest {
    public static void main(String[] args) {
        //System.out.println(recursive(2));
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> remove = new ArrayList<>();
        arrayList.add("1");
        arrayList.add("2");
        arrayList.add("3");
        arrayList.add("4");
        remove.add("1");
        remove.add("3");
        remove.add("5");
        arrayList.retainAll(remove);
    }
    private static int recursive(int number) {
        if (number == 0) {
            return 0;
        }
        //递归算法
        return number + recursive(number - 1);
    }
}
