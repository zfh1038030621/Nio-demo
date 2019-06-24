package com.zfh.nio.v1;

/**
 * @auth zhangfanghui
 * @since 2019-06-15
 */
public class TestNio {
    public static void main(String[] args) throws InterruptedException {
        //开启一个nio服务器，然后使用cmd telnet命令来模拟客户端，实现服务端和客户端的交互
//    SelectSockets selectSockets = new SelectSockets();
//    selectSockets.start();

        new Thread() {
            @Override
            public void run() {
                //开启一个nio服务器，然后使用cmd telnet命令来模拟客户端，实现服务端和客户端的交互
                SelectSockets selectSockets = new SelectSockets();
                selectSockets.start();
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                NioSocketClient client = new NioSocketClient();
                client.start();
            }
        }.start();

//        for (int i = 0; i < 10; i++) {
//            new Thread(){
//                @Override
//                public void run() {
//                    new NioSocketClient().start();
//                }
//            }.start();
//
//            Thread.sleep(100);
//
//        }
    }
}
