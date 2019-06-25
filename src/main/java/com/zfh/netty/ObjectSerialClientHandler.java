package com.zfh.netty;

import com.alibaba.fastjson.JSON;
import com.zfh.netty.bean.SchoolRecord;
import com.zfh.netty.bean.StudentInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Random;

/**
 * @auth zhangfanghui
 * @since 2019-06-25
 * 专门用来测试对象序列号
 */
public class ObjectSerialClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setId(new Random().nextInt());
        studentInfo.setAge(22);
        studentInfo.setName("zfh");
        ctx.writeAndFlush(studentInfo);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SchoolRecord buf = (SchoolRecord) msg;
        System.out.println("【cilent】收到消息主体：" + JSON.toJSONString(buf) + "\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
