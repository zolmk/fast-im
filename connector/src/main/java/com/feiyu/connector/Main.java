package com.feiyu.connector;

import com.feiyu.connector.config.HandlersConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.unix.GenericUnixChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jdk.net.ExtendedSocketOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author zhufeifei 2024/6/2
 **/
public class Main {
  public static void main(String[] args) {
    Properties properties = new Properties();
    try(InputStream is =
          Main.class.getClassLoader().getResourceAsStream("application.properties")) {
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
