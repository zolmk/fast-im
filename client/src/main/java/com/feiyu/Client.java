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

@Slf4j
public class Client {
  public static void main(String[] args) {
    long uid = 2L;
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
    Channel channel = bootstrap.connect("127.0.0.1", 9997).syncUninterruptibly().channel();
    channel.writeAndFlush(Messages.Msg.newBuilder().setType(Messages.MsgType.CONTROL).setControlMsg(Messages.ControlMsg.newBuilder().setType(Messages.ControlType.CLIENT_LOGIN).setClientInfo(Messages.ClientInfo.newBuilder().setCltMsgSeq(0).setUid(uid).build()).build())).syncUninterruptibly();
    channel.writeAndFlush(Messages.Msg.newBuilder().setType(Messages.MsgType.GENERIC).setGenericMsg(Messages.GenericMsg.newBuilder().setCltSeq(1).setPeers(Messages.Peers.newBuilder().setTo(3L).build()).setData(ByteString.copyFrom("test".getBytes())).build()));
    channel.closeFuture().syncUninterruptibly();
    group.shutdownGracefully();
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
          log.info("generic msg: {}", genericMsg);
        } break;
        case NOTICE: {
          Messages.NoticeMsg noticeMsg = msg.getNoticeMsg();
          log.info("notice msg: {}", noticeMsg);
        } break;
      }
    }
  }
}
