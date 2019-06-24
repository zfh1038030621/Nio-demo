package com.zfh.nio.v1;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @auth zhangfanghui
 * @since 2019-06-04
 */
public class SelectSockets {
    public static int DEFUALT_PORT = 8000;
    Selector selector = null;

    public void start() {
        // 打开 ServerSocketChannel 用于监听客户端的连接，它是所有客户端连接的父管道
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            // 监听制定端口，设置连接模式
            serverSocketChannel.socket().bind(new InetSocketAddress(DEFUALT_PORT));
            serverSocketChannel.configureBlocking(false);

            //设置多路复用器selector 创建主线程reactor
            selector = Selector.open();
            //new Thread(new ReactorT);
            // 将ServerSocketChannel注册到selector复用器上，监听accept事件
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 复用器selector epoll轮询
            int num;
             SelectionKey selectionkey = null;
            while (true) {
                // 每隔1s遍历selector
                 num = selector.select(1000);
                if (num == 0)
                    continue;  // 没有可以轮询的对象
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                while (it.hasNext()) {
                    selectionkey = it.next();
                    if (selectionkey.isAcceptable()) {
                        // 有客户端连接
                        doAccept(selectionkey);
                    } else if (selectionkey.isReadable()) {
                        // 有读操作
                        doRead(selectionkey);
                    } else if (selectionkey.isWritable()) {
                        // 有写操作
                        doWrite(selectionkey,"服务器发消息到客户端\n");

                    }
                    it.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();

            // 非正常中断虚拟机
           // System.exit(1);
        }
    }

    /**
     * 对新客户端的连接进行处理
     * @param key
     * @throws IOException
     */
    private void doAccept(SelectionKey key) throws IOException {
        // 获取服务端channel
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if(socketChannel != null){
            socketChannel.configureBlocking(false);
            //将客户端channel设置为非阻塞
            //在和客户端连接成功之后，为了能接受搭配客户端的消息，已经能让客户端读取消息，需要给通道设置相应的权限
           // socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            socketChannel.register(selector, SelectionKey.OP_READ );
            System.out.println("【server】有客户端接入\n");
            System.out.printf("【server】新客户端地址："+(socketChannel.getRemoteAddress().toString())+"\n");
        }

    }

    /**
     *
     */
    private  void doRead(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        int readBytes = 0;
        try{
            readBytes = socketChannel.read(readBuffer);
        }catch (IOException e){
            if(e.getMessage().equals("Connection reset by peer")){
                // 客户端主动断开连接
                System.out.printf("客户端断开连接，地址："+socketChannel.getRemoteAddress());
            }
        }
        if(readBytes > 0){
            // 将缓存区里面到字节数组到指针设置到开始序列设置为下标为0，也就是从源头开始遍历
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            // 将缓冲区里面的字节数组复制到新到字节数组里
            readBuffer.get(bytes);
            String readInfo = new String(bytes,"UTF-8");
            if(StringUtils.isNotEmpty(readInfo)){
                System.out.println("【server read】读取的信息："+readInfo);
                //回复客户端
                doWrite(key,"服务端已经收到你的信息\n");
            }
        }else if(readBytes < 0 ){
         // -1 表明链路关闭，需要关闭socketChannel ，释放资源
            key.cancel();
            socketChannel.close();

        }else{
         // 等于0 没有读取到字节，属于正常场景，忽略
        }

    }

    /**
     * 写消息到客户端
     * @param selectionKey
     * @param response
     * @throws IOException
     */
    private void doWrite( SelectionKey selectionKey ,String response) throws IOException {
        System.out.println("【server write】写消息："+response+"\n");
       if(response != null && !response .equals("")){
           SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
           byte[] bytes  = response.getBytes("UTF-8");
           ByteBuffer byteBUffer = ByteBuffer.wrap(bytes);
           if(byteBUffer.hasRemaining()){
               socketChannel.write(byteBUffer);
           }
       }
    }
//    public static void main(String[] args) {
//        System.out.println(2 << 2);
//    }

}
