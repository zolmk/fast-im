package com.feiyu.core;

import com.feiyu.core.communicate.Constants;
import com.feiyu.core.communicate.proto.ClientProto;
import com.google.protobuf.ByteString;
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

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhufeifei 2023/9/12
 **/

public class TestClient {
    private static Logger LOG = LoggerFactory.getLogger(TestClient.class);
    private static NioEventLoopGroup worker = new NioEventLoopGroup(1);
    private static AtomicBoolean connected = new AtomicBoolean(false);
    private static Channel channel;
    public static void main(String[] args) throws InterruptedException {
        int port = Integer.parseInt(args[0]);
        String toId = args[1];
        String selfId = args[2];
        start(port);
        register(selfId);
        Thread.sleep(100);
        channel.writeAndFlush(groupNotification());
        if (connected.get()) {
            String row = null;
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                row = scanner.nextLine();
                ClientProto.CInner inner = buildMsg(row, toId, selfId);
                System.out.println(inner);
                channel.writeAndFlush(inner);
            }
        }
        LOG.info("finish.");
        close();
    }

    public static ClientProto.CInner buildMsg(String msg, String to, String from) {
        return ClientProto.CInner.newBuilder().setType(ClientProto.DataType.MSG).setMsg(
                ClientProto.Msg.newBuilder().setType(ClientProto.MsgType.TEXT).setData(ByteString.copyFrom(msg.getBytes(StandardCharsets.UTF_8)))
                        .setTo("435").setFrom(from).setIsGroup(true).build()).build();
    }

    public static ClientProto.CInner groupNotification() {
        ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
        builder.setAck(10000).setType(ClientProto.DataType.GROUP_OP).setGroupOp(ClientProto.GroupOp.newBuilder().setOpCode(Constants.GroupOpCode.CREATE).setCreteData(ClientProto.GroupCreateData.newBuilder().setIsPublish(true).setUid("fy")).build());
        return builder.build();
    }

    public static void start(int port) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new IdleStateHandler(10, 10, 0))
                                .addLast(new ProtobufVarint32FrameDecoder())
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                .addLast(new ProtobufDecoder(ClientProto.CInner.getDefaultInstance()))
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
                ClientProto.Notification.newBuilder().setCode(1).setMsg(selfId).build()
        ).build());
    }

    public static void close() {
        worker.shutdownGracefully();
        if (channel != null) channel.close();
    }
}
