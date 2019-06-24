package com.zfh.nio.v1;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @auth zhangfanghui
 * @since 2019-06-15
 */
public class NioSocketClient implements ServerSock {
    SocketChannel socketChannel = null;
    Selector selector;
    Socket socket;
    SocketAddress socketAddress;
    String ip = "127.0.0.1";
    int port = 8000;
    boolean isConnection = false;

    public void start() {
        try {
            // 打开 SocketChannel 绑定客户端地址
            socketChannel = SocketChannel.open();
            //设置SocketChannel为非组撒，并设置连接到tcp参数
            socketChannel.configureBlocking(false);
            socket = socketChannel.socket();
            socket.setReceiveBufferSize(1024);
            socket.setSendBufferSize(1024);

            //连接服务器的socket地址
            socketAddress = new InetSocketAddress(ip, port);
            //连接服务器
            isConnection = socketChannel.connect(socketAddress);
            selector = Selector.open();
            if (isConnection) {
                //如果连接服务器成功，则注册到复用器上
                socketChannel.register(selector, SelectionKey.OP_READ);
            } else {
                //如果没有连接成功，说明客户端已经发送sync包，只是服务器没有返回ack，也就是三次握手没有成功，物理链路没有建立成功
                //向复用器selector注册op——connect状态位，监听服务端到tcp ack应答
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }

            //跟服务器一样，无限轮询复用器，查看其准备好到key
            while (true) {
                int num = selector.select();
                if (num > 0) {
                    Iterator ite = selector.selectedKeys().iterator();
                    while (ite.hasNext()) {
                        SelectionKey key = (SelectionKey) ite.next();
                        ite.remove();

                        //连接处理事件
                        if (key.isConnectable()) {
                            //检查线路是不是正在进行
                            if (socketChannel.isConnectionPending()) {
                                if (socketChannel.finishConnect()) {
                                    //如果连接成功后注册OP_READ事件
                                    key.interestOps(SelectionKey.OP_READ);
                                    doWrite(key,"你好服务端，我是客户端\n");
                                    // socketChannel.write(CharsetHelper.encode(CharBuffer.wrap(getWord())));
                                } else {
                                    key.cancel();
                                }
                            }
                        } else if (key.isReadable()) {
                            // 读服务端到消息
                            doRead(key);

                        } else if (key.isWritable()) {
                            //写消息到服务端
                        }

                    }

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
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
                // 断开连接
                System.out.println("【client】断开连接，地址："+socketChannel.getRemoteAddress()+"\n");
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
                System.out.println("【client】客户端读取的信息："+readInfo+"\n");
                //回复客户端
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
     * 写消息到服务端
     * @param selectionKey
     * @param response
     * @throws IOException
     */
    private void doWrite( SelectionKey selectionKey ,String response) throws IOException {
        if(response != null && !response .equals("")){
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            byte[] bytes  = response.getBytes("UTF-8");
            ByteBuffer byteBUffer = ByteBuffer.wrap(bytes);
            if(byteBUffer.hasRemaining()){
                System.out.printf("【client】写消息到服务端\n");
                socketChannel.write(byteBUffer);
            }
        }
    }

    public void stop() {
        try {
            if (selector != null && selector.isOpen()) {
                selector.close();
            }
            if (socketChannel != null && socketChannel.isOpen()) {
                socketChannel.close();
            }
        } catch (Exception e) {

        }
    }
}
