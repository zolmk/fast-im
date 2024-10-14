package com.feiyu;


import com.feiyu.base.proto.Messages;
import com.feiyu.interfaces.idl.IMessageHandleService;
import com.google.protobuf.ByteString;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class Main implements CommandLineRunner {
  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @DubboReference
  private IMessageHandleService messageHandleService;

  @Override
  public void run(String... args) throws Exception {
    messageHandleService.handle(Messages.Msg.newBuilder().setType(Messages.MsgType.GENERIC).setGenericMsg(Messages.GenericMsg.newBuilder().setData(ByteString.copyFrom("hello".getBytes())).build()).build());
  }
}