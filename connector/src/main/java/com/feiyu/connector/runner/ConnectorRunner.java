package com.feiyu.connector.runner;

import com.feiyu.connector.config.ConnectorConfig;
import com.feiyu.connector.config.HandlersConfig;
import com.feiyu.connector.service.ConnectorWorker;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioChannelOption;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 连接器Runner
 * 连接器核心类，创建Netty NioChannel的入口，负责管理客户端连接
 */
@Slf4j
@Component
@Order(value = 9999)
public class ConnectorRunner implements ApplicationRunner, DisposableBean {

  private final ConnectorConfig connectorConfig;
  private final ConnectorWorker connectorWorker;

  private EventLoopGroup bossGroup;
  private EventLoopGroup workerGroup;
  private Channel mainChannel;

  public ConnectorRunner(ConnectorConfig connectorConfig, ConnectorWorker connectorWorker) {
    this.connectorConfig = connectorConfig;
    this.connectorWorker = connectorWorker;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    ServerBootstrap bootstrap = new ServerBootstrap();
    bossGroup = new NioEventLoopGroup(connectorConfig.getBossCount());
    workerGroup = new NioEventLoopGroup(connectorConfig.getWorkerCount());
    bootstrap.group(bossGroup, workerGroup)
      .channel(NioServerSocketChannel.class)
      .handler(new LoggingHandler(LogLevel.DEBUG))

      .option(ChannelOption.SO_BACKLOG, connectorConfig.getSoBacklog())
      .option(ChannelOption.SO_REUSEADDR, true)

      .childOption(ChannelOption.SO_KEEPALIVE, true)
      .childOption(ChannelOption.TCP_NODELAY, true);
    bootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel channel) throws Exception {
        HandlersConfig.initPipeline(channel.pipeline());
      }
    });
    try {
      ChannelFuture bindFuture = bootstrap.bind(connectorConfig.getAcceptPort());
      bindFuture.addListener((ChannelFutureListener) channelFuture -> {
        if (channelFuture.isSuccess()) {
          log.info("port {} bind success.", connectorConfig.getAcceptPort());
        } else {
          log.error("port {} bind fail.", connectorConfig.getAcceptPort());
        }
      });
      mainChannel = bindFuture.sync().channel();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    connectorWorker.start();
  }

  @Override
  public void destroy() throws Exception {
    if (mainChannel != null && mainChannel.isOpen()) {
      mainChannel.close();
    }
    bossGroup.shutdownGracefully();
    workerGroup.shutdownGracefully();
  }
}
