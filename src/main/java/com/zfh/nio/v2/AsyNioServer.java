package com.zfh.nio.v2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @auth zhangfanghui
 * @since 2019-06-19
 */
public class AsyNioServer {
    private int DEFAULT_PORT = 8000;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    CountDownLatch latch;

    AsyNioServer(int port){
        if(port == 0)
            port = DEFAULT_PORT;
        try {
            asynchronousServerSocketChannel =AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            latch = new CountDownLatch(1);
            doAccept();
            latch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    private void doAccept(){
        asynchronousServerSocketChannel.accept(this,new AcceptCompletionHandler());
    }
}
