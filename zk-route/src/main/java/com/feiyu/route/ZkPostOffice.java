package com.feiyu.route;

import com.feiyu.base.FastImThreadFactory;
import com.feiyu.base.R;
import com.feiyu.base.Result;
import com.feiyu.base.UserEventPublisher;
import com.feiyu.interfaces.idl.ISequenceService;
import com.feiyu.interfaces.idl.SequenceReq;
import com.google.protobuf.MessageLite;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

/**
 * @author zhufeifei 2024/6/8
 **/

@Component
@Log
public class ZkPostOffice implements PostOffice<MessageLite, Long, MessageLite, Channel>, Closeable {
  @DubboReference
  private ISequenceService sequenceService;

  private Map<Long, LetterBox<MessageLite, MessageLite>> map;

  private Map<String, LetterBox<MessageLite, MessageLite>> serverMap;

  private final ZkPostOffice self;

  private final ServiceFinder serviceFinder;

  private final ReentrantLock mainLock;

  private final ThreadPoolExecutor mainExecutor;

  private UserEventPublisher<Long, Channel> userEventPublisher;

  private int subscriberId = -1;

  public ZkPostOffice(ServiceFinder serviceFinder) {
    this.serviceFinder = serviceFinder;
    this.map = new ConcurrentHashMap<>();
    self = this;
    this.mainLock = new ReentrantLock();
    ThreadFactory threadFactory = new FastImThreadFactory("zkPostOffice", true);
    this.mainExecutor = new ThreadPoolExecutor(2, 3, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
  }


  @Override
  public void register(UserEventPublisher<Long, Channel> publisher) {
    this.userEventPublisher = publisher;
    publisher.add(this);
  }

  @Override
  public void login(Long aLong, Channel channel) {
    LetterBox<MessageLite, MessageLite> letterBox = new ClientLetterBox(channel);
    this.serviceFinder.registerClient(aLong.toString());
    this.map.put(aLong, letterBox);
  }

  @Override
  public void logout(Long aLong) {
    this.serviceFinder.unregisterClient(aLong.toString());
    this.map.remove(aLong);
  }

  @Override
  public Optional<Long> nextMsgSeq(Long aLong) {
    long gen = sequenceService.gen(SequenceReq.newBuilder().setUid(aLong).build()).getSeq();
    if (gen < 0) {
      return Optional.empty();
    }
    return Optional.of(gen);
  }

  @Override
  public void ack(Long msgId, int state) {
    //TODO：消息可靠传输，消息确认机制

  }

  @Override
  public LetterBox<MessageLite, MessageLite> getLetterBox(Long aLong) {
    LetterBox<MessageLite, MessageLite> lt = this.map.get(aLong);
    if (lt == null) {

    }
    return lt;
  }

  private LetterBox<MessageLite, MessageLite> createServerLetterBox(Long uid) {
    String serviceId = this.serviceFinder.findService(uid.toString());
    if (this.serverMap.containsKey(serviceId)) {
      return this.serverMap.get(serviceId);
    }
    // create a new server letterbox
    ServerLetterBox serverLetterBox;
    try {
      this.mainLock.lock();
      if (this.serverMap.containsKey(serviceId)) {
        return this.serverMap.get(serviceId);
      }
      serverLetterBox = new ServerLetterBox();
      this.serverMap.put(serviceId, serverLetterBox);
    } finally {
      this.mainLock.unlock();
    }

    this.mainExecutor.execute(() -> {
      InetSocketAddress address = this.serviceFinder.getServiceAddress(serviceId);
      Channel channel = connectPeer(address);
      serverLetterBox.setChannel(channel);
      serverLetterBox.setState(LetterBoxStateEnum.NORMAL);
      serverLetterBox.redeliverAll();
    });
    return serverLetterBox;
  }

  private Channel connectPeer(InetSocketAddress address) {
    ThreadFactory threadFactory = new FastImThreadFactory("fast-im peer", true);
    EventLoopGroup loopGroup = new NioEventLoopGroup(1, threadFactory);
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(loopGroup);

    bootstrap.remoteAddress(address);
    bootstrap.connect();
    // TODO: 需要完善连接对等服务流程，这里或许可以创建一个连接池，两个服务之间创建一条连接可能不太够。
    return null;
  }

  private ChannelInitializer<NioSocketChannel> initializer() {
    return new ChannelInitializer<NioSocketChannel>() {
      @Override
      protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast(new CombinedChannelDuplexHandler<>(new ProtobufVarint32FrameDecoder(), new ProtobufVarint32LengthFieldPrepender()));
        // TODO：需要补齐处理器
      }
    };
  }


  private final BiFunction<MessageLite, Callback<MessageLite>, ChannelFutureListener> deliverListenerFunction = (messageLite, callback) -> channelFuture -> {
    try {
      if (channelFuture.isSuccess()) {
        if (callback != null) {
          callback.success(messageLite);
        }
      }
    } catch (Exception e) {
      log.info("An exception is generated while processing a message callback. " + e.getLocalizedMessage());
      if (callback != null) {
        callback.fail(messageLite);
      }
    }
  };

  @Override
  public void close() throws IOException {
    int sid = this.subscriberId;
    if (sid != -1) {
      this.userEventPublisher.remove(sid);
    }
  }


  /**
   * 同等级的 fast-im 节点信箱的实现
   *
   * @author Zhuff
   */
  class ServerLetterBox implements PostOffice.LetterBox<MessageLite, MessageLite> {
    private final ZkPostOffice postOffice;
    @Setter
    private Channel channel;
    @Setter
    private volatile LetterBoxStateEnum state;

    // It is used when the letterbox is in the creating state.
    private final Queue<Object[]> waitQueue = new ConcurrentLinkedDeque<>();
    /**
     * 等待队列中，最多等待存放的消息个数
     */
    public static final int MAX_MSG_CNT = 20000;

    public ServerLetterBox() {
      this.postOffice = self;
      this.state = LetterBoxStateEnum.CREATING;
    }

    @Override
    public void deliver(MessageLite messageLite, Callback<MessageLite> callback) {
      if (LetterBoxStateEnum.NORMAL.equals(state)) {
        this.channel.writeAndFlush(messageLite).addListener(deliverListenerFunction.apply(messageLite, callback));
        return;
      }
      if (LetterBoxStateEnum.CREATING.equals(state)) {
        if (this.waitQueue.size() <= MAX_MSG_CNT) {
          this.waitQueue.offer(new Object[]{messageLite, callback});
          return;
        }
        // failed.
        if (callback != null) {
          callback.fail(messageLite);
        }
        log.info("The queue is full. Send message failed.");
        return;
      }
      log.info("Channel is closed. Send message failed.");
    }

    @Override
    public void notify(MessageLite messageLite) {
      this.deliver(messageLite, null);
    }

    @Override
    public LetterBoxStateEnum state() {
      return this.state;
    }

    @SuppressWarnings("unchecked")
    public void redeliverAll() {
      while (!waitQueue.isEmpty()) {
        Object[] poll = waitQueue.poll();
        deliver((MessageLite) poll[0], (Callback<MessageLite>) poll[1]);
      }
    }
  }


  /**
   * 客户端信箱的实现
   *
   * @author Zhuff
   */
  class ClientLetterBox implements LetterBox<MessageLite, MessageLite> {
    private final Channel channel;
    private final ZkPostOffice postOffice;

    public ClientLetterBox(Channel channel) {
      this.channel = channel;
      this.postOffice = self;
    }

    @Override
    public void deliver(MessageLite messageLite, Callback<MessageLite> callback) {
      channel.writeAndFlush(messageLite).addListener(deliverListenerFunction.apply(messageLite, callback));
    }

    @Override
    public void notify(MessageLite messageLite) {
      channel.writeAndFlush(messageLite);
    }

    @Override
    public LetterBoxStateEnum state() {
      return LetterBoxStateEnum.NORMAL;
    }
  }
}
