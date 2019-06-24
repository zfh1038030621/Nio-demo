package com.zfh.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;

import java.nio.ByteBuffer;

/**
 * @auth zhangfanghui
 * @since 2019-06-20
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String mes = "请求服务端同意访问\n";
        byte[] req = mes.getBytes("UTF-8");
        ByteBuf buffer = Unpooled.buffer(req.length);
        buffer.writeBytes(req);
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("【cilent】收到消息主体：" + body + "\n");

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
