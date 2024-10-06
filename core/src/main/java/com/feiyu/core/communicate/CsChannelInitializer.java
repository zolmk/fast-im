package com.feiyu.core.communicate;

import com.feiyu.core.communicate.config.AcceptorConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author zhufeifei 2023/9/12
 **/

public class CsChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    protected final AcceptorConfig config;

    public CsChannelInitializer(AcceptorConfig config) {
        this.config = config;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline()
                .addLast(new IdleStateHandler(this.config.getReadIdleTime(), this.config.getWriteIdleTime(), 0))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufEncoder())
                .addLast(new ProtobufDecoder(this.config.getMessageLite()));
        ChannelPipeline pipeline = ch.pipeline();
        this.config.handlers().forEach(namedChannelHandlerSupplier -> {
            NamedChannelHandler handler = namedChannelHandlerSupplier.get();
            pipeline.addLast(handler.name(), handler);
        });
    }
}
