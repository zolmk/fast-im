import com.feiyu.base.proto.Messages;
import com.feiyu.connector.config.HandlersConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Test;

import java.util.Scanner;

public class TestClient {
  @Test
  public void client() throws InterruptedException {
    Bootstrap bootstrap = new Bootstrap();
    Channel channel = bootstrap.group(new NioEventLoopGroup(1))
      .channel(NioSocketChannel.class)
      .handler(new ChannelInitializer<NioSocketChannel>() {
        @Override
        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
          HandlersConfig.initPipeline(nioSocketChannel.pipeline());
        }
      }).remoteAddress("127.0.0.1", 8080)
      .connect().sync().channel();
    String[] strings = new String[] {
      "nihao", "hhh", "login"
    };
    for (String s : strings) {
      Messages.Msg.Builder builder = Messages.Msg.newBuilder();
      builder.setType(Messages.MsgType.CONTROL)
        .setControlMsg(Messages.ControlMsg.newBuilder().setType(Messages.ControlType.CLIENT_LOGIN).setClientInfo(Messages.ClientInfo.newBuilder().setUid(123L).setDvcName(s).build()).build());
      channel.writeAndFlush(builder.build());
      Thread.sleep(2000);
    }
    channel.close();
  }
}
