package com.fy.chatserver.communicate;

import com.fy.chatserver.communicate.config.RemoteChannelConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @author zhufeifei 2023/9/9
 **/

public class RemoteChannelFactory {
    public static final Logger LOG = LoggerFactory.getLogger(RemoteChannelFactory.class);

    public static RemoteChannel newInstance(String serviceId, InetSocketAddress isa, RemoteChannelConfig config) throws InterruptedException {
        Bootstrap bootstrap = newBootstrap();
        bootstrap.remoteAddress(isa)
                .handler(new RemoteChannelInitializer(config));
        ChannelFuture future = bootstrap.connect().sync();
        if (future.isSuccess()) {
            return new RemoteChannel(serviceId, future.channel());
        }
        return null;
    }

    private static Bootstrap newBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(new NioEventLoopGroup(2))
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap;
    }

    static class RemoteChannelInitializer extends ChannelInitializer<NioSocketChannel> {
        private final RemoteChannelConfig config;
        public RemoteChannelInitializer(RemoteChannelConfig config) {
            this.config = config;
        }
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
            ch.pipeline()
                    //.addLast(SslHandlerProvider.getSslHandler(this.config))
                    .addLast(new IdleStateHandler( this.config.getReadIdleTime(), this.config.getWriteIdleTime(), 0))
                    .addLast(new ProtobufVarint32FrameDecoder())
                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                    .addLast(new ProtobufEncoder())
                    .addLast(new ProtobufDecoder(this.config.getMessageLite()));
            ChannelPipeline pipeline = ch.pipeline();
            this.config.getHandlers()
                    .forEach(supplier -> {
                        NamedChannelHandler handler = supplier.get();
                        pipeline.addLast(handler.name(), handler);
                    });
        }
    }
}
