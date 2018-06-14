package com.wangjikai.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author 22717.
 * @date 2018/4/2.
 * Description:序列化工具
 */
public class SerializeUtils {
    public static void main(String[] args) {
        String string = "test_wjk";
        byte[] bytes = serialize(string);
        String string1 = (String) unserialize(bytes);
        System.out.println(string1);
    }

    public static byte[] serialize(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            byteArrayOutputStream.close();
            objectOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    public static Object unserialize(byte[] bytes) {
        ByteArrayInputStream bais = null;
        try {
            // 反序列化
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            bais.close();
            ois.close();
            return ois.readObject();
        } catch (Exception e) {
        }
        return null;
    }
}
