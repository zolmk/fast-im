package com.fy.chatserver.communicate;

import com.fy.chatserver.communicate.config.AcceptorConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;

import java.util.function.Supplier;

/**
 * @author zhufeifei 2023/9/12
 **/

public class CsRemoteChannelInitializer extends CsChannelInitializer {
    private final Supplier<SslHandler> sslHandlerSupplier;

    public CsRemoteChannelInitializer(AcceptorConfig config , Supplier<SslHandler> sslHandlerSupplier) {
        super(config);
        this.sslHandlerSupplier = sslHandlerSupplier;

    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(sslHandlerSupplier.get());
        super.initChannel(ch);
    }
}
