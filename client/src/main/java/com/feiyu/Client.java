package com.feiyu;

import com.feiyu.base.proto.ProtocolUtil;
import com.feiyu.base.proto.Messages;
import com.google.protobuf.ByteString;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

@Slf4j
public class Client {
  public static void main(String[] args) {
    long uid = Long.parseLong(args[0]);
    int port = 9996;
    if (args.length > 1) {
      port = Integer.parseInt(args[1]);
    }
    NioEventLoopGroup group = new NioEventLoopGroup(1);
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(group)
      .channel(NioSocketChannel.class)
      .option(ChannelOption.TCP_NODELAY, true)
      .handler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
          ch.pipeline()
            .addLast(new ProtobufVarint32LengthFieldPrepender())
            .addLast(new ProtobufVarint32FrameDecoder())
            .addLast(new ProtobufEncoder())
            .addLast(new ProtobufDecoder(Messages.Msg.getDefaultInstance()))
            .addLast(new MyMsgHandler());
        }
      });
    int cltSeq = 0;
    Channel channel = bootstrap.connect("127.0.0.1", port).syncUninterruptibly().channel();
    channel.writeAndFlush(Messages.Msg.newBuilder().setType(Messages.MsgType.CONTROL).setControlMsg(Messages.ControlMsg.newBuilder().setType(Messages.ControlType.CLIENT_LOGIN).setClientInfo(Messages.ClientInfo.newBuilder().setCltMsgSeq(cltSeq).setUid(uid).build()).build())).syncUninterruptibly();
    Scanner in = new Scanner(System.in);
    while (true) {
      cltSeq ++;
      String s = in.nextLine();
      String[] split = s.split(" ");
      if (split.length != 2) {
        break;
      }
      channel.writeAndFlush(getMsg(uid, Long.parseLong(split[0]), split[1], cltSeq));
    }
    in.close();
    channel.closeFuture().syncUninterruptibly();
    group.shutdownGracefully();
  }

  private static Messages.Msg getMsg(long from, long to, String msg, int cltSeq) {
    return Messages.Msg.newBuilder().setType(Messages.MsgType.GENERIC).setGenericMsg(Messages.GenericMsg.newBuilder().setCltSeq(cltSeq).setPeers(Messages.Peers.newBuilder().setFrom(from).setTo(to).build()).setData(ByteString.copyFrom(msg.getBytes())).build()).build();
  }

  private static class MyMsgHandler extends SimpleChannelInboundHandler<Messages.Msg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Messages.Msg msg) throws Exception {
      switch (msg.getType()) {
        case CONTROL: {
          Messages.ControlMsg controlMsg = msg.getControlMsg();
          if (Messages.ControlType.CONNECT_REST.equals(controlMsg.getType())) {
            log.info("connect rest.");
            ctx.channel().close();
          }
        } break;
        case HEARTBEAT: {
          Messages.HeartbeatMsg heartbeatMsg = msg.getHeartbeatMsg();
          if (Messages.HeartbeatType.PING.equals(heartbeatMsg.getType())) {
            ctx.writeAndFlush(ProtocolUtil.PONG);
          }
        } break;
        case GENERIC: {
          Messages.GenericMsg genericMsg = msg.getGenericMsg();
          Messages.MsgExtraInfo extraInfo = genericMsg.getExtraInfo();
          log.info("generic msg: {}", genericMsg);
          // 消息已送达ack
          ctx.writeAndFlush(ProtocolUtil.messageDeliveredAck(extraInfo.getMsgId(), genericMsg.getPeers().getFrom()));
          ctx.writeAndFlush(ProtocolUtil.messageReadAck(extraInfo.getMsgId(), genericMsg.getPeers().getFrom()));
        } break;
        case NOTICE: {
          log.info("notice msg: {}", msg);
        } break;
      }
    }
  }
}
