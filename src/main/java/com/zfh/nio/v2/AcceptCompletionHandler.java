package com.zfh.nio.v2;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @auth zhangfanghui
 * @since 2019-06-19
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyNioServer> {

    @Override
    public void completed(AsynchronousSocketChannel result, AsyNioServer attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer buffer = ByteBuffer.allocate(1014);
        //TODO
        //result.read(buffer,buffer,new Rea)
    }

    @Override
    public void failed(Throwable exc, AsyNioServer attachment) {

    }
}
