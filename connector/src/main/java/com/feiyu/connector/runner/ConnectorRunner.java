package com.feiyu.connector.runner;

import com.feiyu.connector.Application;
import com.feiyu.connector.config.HandlersConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.GenericUnixChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConnectorRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Properties properties = new Properties();
        try(InputStream is =
                    Application.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int bossCnt = Integer.parseInt(properties.getProperty("server.boss-count"));
        int workerCnt = Integer.parseInt(properties.getProperty("server.worker-count"));
        int port = Integer.parseInt(properties.getProperty("server.port"));

        ServerBootstrap bootstrap = new ServerBootstrap();
        NioEventLoopGroup boss = new NioEventLoopGroup(bossCnt);
        NioEventLoopGroup worker = new NioEventLoopGroup(workerCnt);
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 1000)
                .option(GenericUnixChannelOption.SO_REUSEPORT, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel channel) throws Exception {
                HandlersConfig.initPipeline(channel.pipeline());
            }
        });
        try {
            ChannelFuture bind = bootstrap.bind(port);
            bind.sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}
