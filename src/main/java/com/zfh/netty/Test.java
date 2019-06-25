package com.zfh.netty;

/**
 * @auth zhangfanghui
 * @since 2019-06-21
 */
public class Test {
    public static void main(String[] args) throws InterruptedException {
        new Thread(){
            @Override
            public void run() {
               new TimeServer().bind(8000);
            }
        }.start();


        Thread.sleep(20000L);
        for (int i = 0; i < 30; i++) {
            new Thread(){
                @Override
                public void run() {
                    try {
                        new TimeClient().connect(8000,"127.0.0.1");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
