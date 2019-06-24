package com.zfh.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @auth zhangfanghui
 * @since 2019-06-20
 */
public class TimeServer {
    public void bind(int port) {
        //配置服务端到NIO线程组
        //创建2个线程组（主/从）专门处理网络事件，功能就像NIO到r多路复用器selector
        //这里创造2个的用意是：一个用来专门做接受客户端的连接，一个是专门用于网络读写的
        //主只可以有一个，从可以有多个，主会将连接成功的sockchannel给从，从拿到之后注册到之间到selector中
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // ServerBootstrap是NIO服务端启动辅助类，相关的配置也是在这个类里面去设置
            ServerBootstrap b = new ServerBootstrap();
            //NioServerSocketChannel相当于NIO中ServerSocketChannel
            b.group(bossLoopGroup, workerGroup).channel(NioServerSocketChannel.class)

                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannelHandler());
            //sync方法是等到前面方面执行完才可以执行下面的代码
            ChannelFuture f = b.bind(port).sync();
            //等待服务端链路关闭之后
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // 优雅退出，释放线程池资源
            bossLoopGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            //LineBasedFrameDecoder编解码器以换行符\n或者\r\n作为依据，遇到如下符合就认为完整的消息，如果一直没有遇到换行符就长度到我们设置到长度之后自动抛出异常，同时忽略之前读到到流
            //LineBasedFrameDecoder用来解决tcp粘包，拆包问题
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            // 将流转为字符串
            //   socketChannel.pipeline().addLast(new StringDecoder());
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }
    }
}
