package com.fy.chatserver;

import cn.hutool.core.lang.UUID;
import com.fy.chatserver.communicate.*;
import com.fy.chatserver.communicate.config.SslConfig;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
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

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhufeifei 2023/9/12
 **/

public class TLSTestClient {
    private static Logger LOG = LoggerFactory.getLogger(TLSTestClient.class);
    private static NioEventLoopGroup worker = new NioEventLoopGroup(1);
    private static AtomicBoolean connected = new AtomicBoolean(false);
    private static Channel channel;
    public static void main(String[] args) throws InterruptedException {
        int port = Integer.parseInt(args[0]);
        String toId = args[1];
        start(port);
        Thread.sleep(1000);
        String selfId = UUID.fastUUID().toString();
        register(selfId);
        if (connected.get()) {
            String row = null;
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                row = scanner.nextLine();
                channel.writeAndFlush(buildMsg(row, toId, selfId)).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            LOG.info("发送成功");
                        } else {
                            LOG.info("发送失败 {}", future.cause().getMessage());
                        }
                    }
                });
            }
        }
        LOG.info("finish.");
        close();
    }

    public static ServerProto.SInner buildMsg(String msg, String to, String from) {
        return ServerProto.SInner.newBuilder().setType(ClientProto.DataType.MSG).build();
    }



    public static void start(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        final SslConfig sslConfig = new SslConfig();
                        sslConfig.setCertAlias("chat-server");
                        sslConfig.setKeyStoreAlias("chat-server");
                        sslConfig.setPrivateKeyAlias("chat-server");
                        sslConfig.setPrivatePassword("123456");
                        sslConfig.setKeyStorePassword("123456");
                        sslConfig.setJksPath("chat-server.jks");
                        sslConfig.setCerPath("/Users/zhufeifei/Documents/secretkey/chat-server/chat-server.cer");
                        ch.pipeline()
                                .addLast(SslHandlerProvider.getSslHandler(sslConfig))
                                .addLast(new IdleStateHandler(10, 10, 0))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufDecoder(ServerProto.SInner.getDefaultInstance()))
                                .addLast(new ProtobufEncoder())
                                .addLast(new SimpleChannelInboundHandler<ClientProto.CInner>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, ClientProto.CInner msg) throws Exception {
                                        LOG.info("receive: {}", msg.toString());
                                    }
                                });
                    }
                });
        channel = bootstrap.connect("127.0.0.1", port).sync().addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                LOG.info("Successful connected.");
                connected.set(true);
            } else {
                LOG.info("connect Failed.");
            }
        }).channel();
    }

    public static void register(String selfId) {
        channel.writeAndFlush(ClientProto.CInner.newBuilder().setType(ClientProto.DataType.NOTIFICATION).setNotification(
                ClientProto.Notification.newBuilder().setCode(0).setMsg(selfId).build()
        ).build());
    }

    public static void close() {
        worker.shutdownGracefully();
        if (channel != null) channel.close();
    }
}
