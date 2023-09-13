package com.fy.chatserver.communicate;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fy.chatserver.common.CircleSet;
import com.fy.chatserver.common.CsThreadFactory;
import com.fy.chatserver.common.Dispatcher;
import com.fy.chatserver.common.SyncCircleSet;
import com.fy.chatserver.communicate.config.AcceptorConfig;
import com.fy.chatserver.communicate.config.RemoteAcceptorConfig;
import com.fy.chatserver.communicate.config.SslConfig;
import com.fy.chatserver.communicate.config.UserAcceptorConfig;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.fy.chatserver.communicate.utils.ProtocolUtil;
import com.fy.chatserver.discovery.ServiceFinder;
import com.fy.chatserver.discovery.ServiceFinderProvider;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhufeifei 2023/9/9
 **/

public class MsgManager implements Closeable {
    private final static Logger LOG = LoggerFactory.getLogger(MsgManager.class);
    private final AtomicInteger outDispatcherCount = new AtomicInteger(1);
    private final AtomicInteger inDispatcherCount = new AtomicInteger(1);
    private final int defaultDispatcherCount = 2;

    private final int defaultOutAndInQueueCapacity = 200;
    private final String curServiceId;

    private final ConcurrentMap<String, UserChannel> userChannelMap;
    private final ConcurrentHashSet<String> userChannelIdsSet;
    private final ConcurrentMap<String, RemotePeer> remotePeerMap;

    private final CircleSet<OutDispatcher> outDispatchers;
    private final CircleSet<InDispatcher> inDispatchers;
    private final ChannelChecker channelChecker;

    private final ServiceFinder serviceFinder;

    private final Accepter userAccepter;

    private final SslConfig sslConfig;
    private final Accepter remoteAccepter;


    public MsgManager(Properties properties) {
        this.remotePeerMap = new ConcurrentHashMap<>();

        this.userChannelIdsSet = new ConcurrentHashSet<>();
        this.userChannelMap = new SyncSetConcurrentHashMap<>(this.userChannelIdsSet);

        this.channelChecker = new ChannelChecker();
        this.outDispatchers = new SyncCircleSet<>();
        this.inDispatchers = new SyncCircleSet<>();

        AcceptorConfig userAcceptorConfig = new UserAcceptorConfig(UserChannelHandler::new);
        userAcceptorConfig.load(properties);

        this.sslConfig = new SslConfig();
        this.sslConfig.load(properties);

        AcceptorConfig remoteAcceptorConfig = new RemoteAcceptorConfig(RemoteChannelHandler::new);
        remoteAcceptorConfig.load(properties);

        this.curServiceId = properties.getProperty("chat-server.service-id", "chat-server-0");


        this.userAccepter = new Accepter(userAcceptorConfig, ChannelInitializerProvider.forUser(userAcceptorConfig));
        this.remoteAccepter = new Accepter(remoteAcceptorConfig, ChannelInitializerProvider.forRemoteServer(remoteAcceptorConfig));

        String serviceFindProviderClass = properties.getProperty("chat-server.service-finder.provider", "com.fy.chatserver.discovery.ZKServiceFinderProvider");
        try {
            Class<?> aClass = Class.forName(serviceFindProviderClass);
            ServiceFinderProvider provider = (ServiceFinderProvider) aClass.newInstance();
            this.serviceFinder = provider.newInstance(properties);
            this.serviceFinder.registerServer(this.curServiceId, properties.getProperty("chat-server.ip"));
        } catch (ClassNotFoundException e) {
            LOG.error("{} configuration is error, {} class not found.", "chat-server.service-finder.provider", serviceFindProviderClass);
            throw new RuntimeException(e);
        } catch (InstantiationException | IllegalAccessException e) {
            LOG.error("{} class must have empty constructor.", serviceFindProviderClass);
            throw new RuntimeException(e);
        }
    }

    public void start() throws InterruptedException {
        this.userAccepter.start();
        this.remoteAccepter.start();
        this.initDispatcher();
    }

    private void initDispatcher() {
        InDispatcher inDispatcher;
        OutDispatcher outDispatcher;
        for (int i = 0; i < 2; i++) {
            inDispatcher = new InDispatcher();
            CsThreadFactory.getInstance().newThread(inDispatcher).start();
            this.inDispatchers.add(inDispatcher);

            outDispatcher = new OutDispatcher();
            CsThreadFactory.getInstance().newThread(outDispatcher).start();
            this.outDispatchers.add(outDispatcher);
        }
    }

    /**
     * put the next InDispatcher
     * @param protocol protocol data
     */
    private void nextIn(ClientProto.CInner protocol) {
        InDispatcher inDispatcher = this.inDispatchers.next();
        if (inDispatcher == null) {
            LOG.debug("out-dispatcher may be not start.");
            return;
        }
        while (!inDispatcher.offer(protocol)) {
            inDispatcher = this.inDispatchers.next();
        }
    }

    /**
     * put the next OutDispatcher
     * @param protocol protocol data
     */
    private void nextOut(ClientProto.CInner protocol) {
        OutDispatcher outDispatcher = this.outDispatchers.next();
        if (outDispatcher == null) {
            LOG.debug("out-dispatcher may be not start.");
            return;
        }
        while (!outDispatcher.offer(protocol)) {
            outDispatcher = this.outDispatchers.next();
        }
    }

    private SslConfig getRemoteChannelConfig(String serviceId) {
        return this.sslConfig;
    }


    @Override
    public void close() throws IOException {
        closeAll(remotePeerMap.values().iterator());
        closeAll(outDispatchers.iter());
        closeAll(inDispatchers.iter());
        serviceFinder.close();
        channelChecker.close();
        this.userAccepter.close();
        this.remoteAccepter.close();
    }

    private void closeAll(Iterator<? extends Closeable> iterator) throws IOException {
        Closeable closeable = null;
        while (iterator.hasNext()) {
            try {
                closeable = iterator.next();
                closeable.close();
            } catch (Exception e) {
                LOG.error("occur error when closing {}, error {}", closeable, e);
            }
        }
    }

    class ChannelChecker implements Closeable {
        private MsgManager self = MsgManager.this;
        public boolean isLocal(String toId) {
            return self.userChannelIdsSet.contains(toId);
        }

        @Override
        public void close() throws IOException {
            this.self.userChannelIdsSet.clear();
            this.self = null;

        }
    }

    static class SyncSetConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {
        private final ConcurrentHashSet<K> set;
        public SyncSetConcurrentHashMap(ConcurrentHashSet<K> set) {
            this.set = set;
        }
        @Override
        public V put(@Nonnull K key,@Nonnull V value) {
            this.set.add(key);
            return super.put(key, value);
        }

        @Override
        public V remove(@Nonnull Object key) {
            this.set.remove(key);
            return super.remove(key);
        }
    }


    class InDispatcher extends Dispatcher<ClientProto.CInner> implements Runnable {
        MsgManager self = MsgManager.this;
        public InDispatcher() {
            super(MsgManager.this.defaultOutAndInQueueCapacity, LoggerFactory.getLogger(InDispatcher.class), MsgManager.this.inDispatcherCount.getAndIncrement());
        }

        @Override
        protected void oneLoop(ClientProto.CInner protocol) throws InterruptedException {
            if (!protocol.hasMsg()) {
                LOG.info("{} have not the msg object.", protocol);
                return;
            }
            String toId = protocol.getMsg().getTo();
            if (!MsgManager.this.channelChecker.isLocal(toId)) {
                MsgManager.this.nextOut(protocol);
                return;
            }
            UserChannel userChannel = self.userChannelMap.get(toId);
            userChannel.writeAndFlush(protocol);
        }

        @Override
        public String namePrefix() {
            return "in";
        }
    }

    class OutDispatcher extends Dispatcher<ClientProto.CInner> {
        MsgManager self = MsgManager.this;
        public OutDispatcher() {
            super(MsgManager.this.defaultOutAndInQueueCapacity, LoggerFactory.getLogger(OutDispatcher.class), MsgManager.this.outDispatcherCount.getAndIncrement());
        }

        @Override
        protected void oneLoop(final ClientProto.CInner protocol) throws InterruptedException {
            if (!protocol.hasMsg()) {
                LOG.info("{} have not the msg object.", protocol);
                return;
            }
            String toId = protocol.getMsg().getTo();
            if (MsgManager.this.channelChecker.isLocal(toId)) {
                MsgManager.this.nextIn(protocol);
                return;
            }
            String serviceId = serviceFinder.findService(toId);
            RemotePeer remotePeer = MsgManager.this.remotePeerMap.get(serviceId);
            if (remotePeer != null || (remotePeer = MsgManager.this.createAndStartRemotePeer(serviceId)) != null) {
                remotePeer.write(protocol).addListener(future -> {
                    if (future.isSuccess()) {
                        LOG.info("send success. {}", protocol);
                    } else {
                        LOG.info("send fail. {}", future.cause().getMessage());
                        self.nextOut(protocol);
                    }
                });
                return;
            }
            // TODO: record the message for the future
            LOG.info("{} discard.", protocol);
        }
        @Override
        public String namePrefix() {
            return "out";
        }
    }

    private RemotePeer createAndStartRemotePeer(String serviceId) {
        SslConfig config = this.getRemoteChannelConfig(serviceId);
        InetSocketAddress isa = this.serviceFinder.getServiceAddress(serviceId);
        if (isa == null) {
            return null;
        }
        try {
            IChannel iChannel = RemoteChannelFactory.newInstance(serviceId, isa, new RemoteChannelHandler(), config);
            if (iChannel == null) return null;
            DefaultRemotePeerImpl remotePeer = new DefaultRemotePeerImpl(serviceId, iChannel);
            CsThreadFactory.getInstance().newThread(remotePeer).start();
            this.remotePeerMap.put(serviceId, remotePeer);
            return remotePeer;
        } catch (InterruptedException e) {
            LOG.error("create remote peer occur error", e);
            return null;
        }

    }

    class RemoteChannelHandler extends ChannelDuplexHandler implements NamedChannelHandler {
        private final MsgManager self = MsgManager.this;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //TODO:互相交换身份信息，并在失去连接时，将自身从Map中删除
            super.channelActive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ServerProto.SInner) {
                ClientProto.CInner cInner = ProtocolUtil.s2c((ServerProto.SInner) msg);
                MsgManager.this.nextIn(cInner);
            }
            super.channelRead(ctx, msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOG.error("exception: {}", cause.getMessage());
            super.exceptionCaught(ctx, cause);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            LOG.info("channel unregistered.");
            super.channelUnregistered(ctx);
        }

        @Override
        public String name() {
            return "remote-handler";
        }
    }

    class UserChannelHandler extends ChannelDuplexHandler implements NamedChannelHandler {
        private boolean isRegister;
        private final MsgManager self;
        public UserChannelHandler() {
            this.isRegister = false;
            this.self = MsgManager.this;
        }
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ClientProto.CInner) {
                ClientProto.CInner inner = (ClientProto.CInner) msg;
                if (inner.getType() == ClientProto.DataType.MSG) {
                    nextOut(inner);
                    if (!isRegister) {
                        sendRegisterNotification(ctx);
                    }
                    return;
                }
                switch (inner.getType()) {
                    case HEARTBEAT:break;
                    case NOTIFICATION: {
                        handlerNotification(ctx,inner);
                    } break;
                    case UNRECOGNIZED: {
                        LOG.info("unknown message {}", inner);
                    }break;
                }
            }

            super.channelRead(ctx, msg);
        }

        private void sendRegisterNotification(ChannelHandlerContext ctx) {
            ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
            builder.setType(ClientProto.DataType.NOTIFICATION)
                    .setNotification(ClientProto.Notification.newBuilder().setCode(0).build());
            ctx.writeAndFlush(builder.build());
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOG.error("user channelHandler occur exception.", cause);
        }

        private void handlerNotification(ChannelHandlerContext ctx, ClientProto.CInner inner) {
            ClientProto.Notification notification = inner.getNotification();
            if (notification.getCode() == 0 && !this.isRegister) {
                // register the channel
                String clientId = notification.getMsg();
                UserChannel userChannel = new UserChannel(self.curServiceId, ctx.channel());
                self.serviceFinder.registerClient(self.curServiceId, clientId);
                self.userChannelMap.put(clientId, userChannel);
                this.isRegister = true;
            }
        }

        @Override
        public String name() {
            return "user-handler";
        }
    }
}
