package com.zfh.netty;

import com.alibaba.fastjson.JSON;
import com.zfh.netty.bean.SchoolRecord;
import com.zfh.netty.bean.StudentInfo;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.MathUtil;

import java.util.Random;

/**
 * @auth zhangfanghui
 * @since 2019-06-25
 * 专门用来测试对象序列号
 */
public class ObjectSerialServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("注册了新客户端： "+ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        StudentInfo studentInfo = (StudentInfo) msg;
        System.out.println("【server】收到消息主体："+ JSON.toJSONString(studentInfo));

        int score = new Random().nextInt(100);
        SchoolRecord record = new SchoolRecord();
        record.setScore(score);
        record.setId((int)Math.random());
        record.setStId(studentInfo.getId());
        ctx.writeAndFlush(record);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
