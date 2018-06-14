package com.wangjikai.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author 22717.
 * @date 2018/4/17.
 * Description:Java IO流测试
 */
public class JavaIO {
    public static void main(String[] args) {
        //输入流、读取文件。抽象方法read读取下一字节，读到文件末尾返回-1.
        //如果流中没有数据read就会阻塞直至数据到来或异常出现或流关闭
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("E:/test/test.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] bytes = new byte[6];
        int x = 0;
        try {
            x = inputStream.read(bytes,0,bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(x);
        System.out.println(new String(bytes));
    }
}
