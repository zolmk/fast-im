package com.feiyu.core.communicate;

import com.feiyu.core.communicate.config.AcceptorConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author zhufeifei 2023/9/10
 **/

public class Accepter implements Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(Accepter.class);
    private final NioEventLoopGroup bosses;
    private final NioEventLoopGroup workers;
    private final ServerBootstrap bootstrap;
    private final AcceptorConfig config;
    private final ChannelInitializer<? extends Channel> channelInitializer;

    public Accepter(AcceptorConfig config, ChannelInitializer<? extends Channel> channelInitializer) {
        this.bosses = new NioEventLoopGroup(config.getBossCount());
        this.workers = new NioEventLoopGroup(config.getWorkerCount());
        this.bootstrap = new ServerBootstrap();
        this.config = config;
        this.channelInitializer = channelInitializer;
    }

    public void start() throws InterruptedException {
        initialize();
        this.bootstrap.bind(this.config.getPort()).sync().addListener(future -> {
            if (future.isSuccess()) {
                LOG.info("user accepter running.");
            } else {
                LOG.error("user accepter occur error.");
                LOG.error("the server should be stop.");
            }
        });
    }

    private void initialize() {
        this.bootstrap.channel(NioServerSocketChannel.class)
                .group(bosses, workers)
                .option(ChannelOption.SO_BACKLOG, config.getBacklog())
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(this.channelInitializer);
    }

    public AcceptorConfig getConfig() {
        return config;
    }

    @Override
    public void close() throws IOException {
        this.bosses.shutdownGracefully();
        this.workers.shutdownGracefully();
    }


}
