package com.zfh.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.commons.lang.StringUtils;

import java.net.InetAddress;
import java.nio.charset.Charset;

/**
 * @auth zhangfanghui
 * @since 2019-06-21
 */
public class TimeClient {
    int DEFAULT_PORT = 8000;
    String DEFAULT_IP = "127.0.0.1";

    public void connect(int port, String host) throws InterruptedException {
        //配置客户端NIO线程组
        EventLoopGroup client = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(client).channel(NioSocketChannel.class)
                    //下面是配置socket的一些传输，
                    // TCP_NODELAY就是用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ClientChildChannelHandler());

            ChannelFuture f = b.connect(StringUtils.isNotEmpty(host) ? host : DEFAULT_IP, port != 0 ? port : DEFAULT_PORT).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.shutdownGracefully();
        }

    }

    private class ClientChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            // 这里一定要加如下两行代码，要不然回发生客户端和服务端无法传递数据的问题（对象必须实现serializable接口）
            // 下面两行主要目的就是给传送的对象编码
            //socketChannel.pipeline().addLast(new ObjectEncoder());
//            socketChannel.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE,
//                    ClassResolvers.weakCachingConcurrentResolver(null))); // 最大长度
          //  socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            //增加objectEncoder,objectdecoder
            socketChannel.pipeline().addLast(new ObjectEncoder());
            socketChannel.pipeline().addLast(new ObjectDecoder(1024*1024,ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())));
           // socketChannel.pipeline().addLast(new TimeClientHandler());
            socketChannel.pipeline().addLast(new ObjectSerialClientHandler());
        }
    }
}
