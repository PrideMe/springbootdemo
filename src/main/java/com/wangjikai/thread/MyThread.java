package com.wangjikai.thread;

/**
 * @author 22717.
 * @date 2018/4/4.
 * Description:线程测试
 */
public class MyThread  {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("begin main");
        ThreadTest threadTest = new ThreadTest();
        Thread thread = new Thread(threadTest);
        thread.setName("A-t");
        thread.start();
        Thread.sleep(3000);
        threadTest.setStop();
        System.out.println("end main");
    }
}

class ThreadTest implements Runnable {
    private boolean isStop = false;

    public void setStop() {
        this.isStop = true;
        System.out.println("设置为true");
    }
    @Override
    public void run() {
        while(!isStop)
        {
            System.out.println(Thread.currentThread().getName()+"....run");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("InterruptedException");
            }
        }
    }
}