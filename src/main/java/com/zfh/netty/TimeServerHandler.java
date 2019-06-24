package com.zfh.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.ByteBuffer;

/**
 * @auth zhangfanghui
 * @since 2019-06-20
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("注册了新客户端： "+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req,"UTF-8");
        System.out.println("【server】收到消息主体："+body);

        //回写
        String backContent = "你好，同意访问\n";
        byte[] req1 = backContent.getBytes("UTF-8");
        ByteBuf  buffer= Unpooled.buffer(req1.length);
        buffer.writeBytes(req1);
        ctx.writeAndFlush(buffer);

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // write方法只是把待发送的消息放到发送缓存数组中
        //flush方法才会最终将发送缓存数组中的消息全部写到SocketChannel
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
