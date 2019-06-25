# Nio-demo
nio v1包中是验证nio，测试类为TestNio
netty包里面是验证netty，测试类为Test。如果通过里面没有接收到消息，请注意要在消息末尾加\n换行符，或者将代码中的LineBasedFrameDecoder编解码器去掉


第二阶段：
使用netty的java序列号编解码器对对象进行序列号。
注意点：ObjectEncoder  ObjectDecoder自身已经解决了tcp对粘包，拆包问题
