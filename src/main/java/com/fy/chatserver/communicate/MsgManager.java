package com.fy.chatserver.communicate;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.fy.chatserver.common.CircleSet;
import com.fy.chatserver.common.CsThreadFactory;
import com.fy.chatserver.common.Dispatcher;
import com.fy.chatserver.common.SyncCircleSet;
import com.fy.chatserver.communicate.config.*;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.fy.chatserver.communicate.utils.ProtocolUtil;
import com.fy.chatserver.discovery.ServiceFinder;
import com.fy.chatserver.discovery.ServiceFinderProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

    public MsgManager(Properties properties) {
        this.remotePeerMap = new ConcurrentHashMap<>();

        this.userChannelIdsSet = new ConcurrentHashSet<>();
        this.userChannelMap = new SyncSetConcurrentHashMap<>(this.userChannelIdsSet);

        this.userStateSupervisor = new UserStateSupervisor();
        this.remotePeerStateSupervisor = new RemotePeerStateSupervisor();

        this.outDispatchers = new SyncCircleSet<>(this.defaultDispatcherCount, OutDispatcher::new);
        this.inDispatchers = new SyncCircleSet<>(this.defaultDispatcherCount, InDispatcher::new);

        this.remoteChannelMsgHandler = new RemoteChannelMsgHandler();

        this.userChannelMsgHandler = new UserChannelMsgHandler();

        this.unifiedHeartbeatHandler = new UnifiedHeartbeatHandler();

        AcceptorConfig userAcceptorConfig = new UserAcceptorConfig(UserChannelNotificationHandler::new, ()->this.userChannelMsgHandler, ()->this.unifiedHeartbeatHandler);
        userAcceptorConfig.load(properties);

        this.remoteChannelConfig = new RemoteChannelConfig(this.remoteChannelMsgHandler, new RemoteChannelNotificationHandler(), this.unifiedHeartbeatHandler);
        this.remoteChannelConfig.load(properties);

        AcceptorConfig remoteAcceptorConfig = new RemoteAcceptorConfig(()->this.remoteChannelMsgHandler, RemoteChannelNotificationHandler::new);
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

    private final static Logger LOG = LoggerFactory.getLogger(MsgManager.class);
    private final AtomicInteger outDispatcherCount = new AtomicInteger(1);
    private final AtomicInteger inDispatcherCount = new AtomicInteger(1);
    private final int defaultDispatcherCount = 2;

    private final int defaultOutAndInQueueCapacity = 200;
    private final String curServiceId;

    private final ConcurrentMap<String, IChannel> userChannelMap;
    private final ConcurrentHashSet<String> userChannelIdsSet;
    private final ConcurrentMap<String, RemotePeer> remotePeerMap;

    private final CircleSet<OutDispatcher> outDispatchers;
    private final CircleSet<InDispatcher> inDispatchers;

    private final UserStateSupervisor userStateSupervisor;
    private final RemotePeerStateSupervisor remotePeerStateSupervisor;

    private final ServiceFinder serviceFinder;

    private final Accepter userAccepter;

    private final RemoteChannelConfig remoteChannelConfig;
    private final Accepter remoteAccepter;

    // shared
    private final UserChannelMsgHandler userChannelMsgHandler;

    // shared
    private final RemoteChannelMsgHandler remoteChannelMsgHandler;

    // shared
    private final UnifiedHeartbeatHandler unifiedHeartbeatHandler;


    public void start() throws InterruptedException {
        this.userAccepter.start();
        this.remoteAccepter.start();
        // start dispatcher
        this.inDispatchers.iter().forEachRemaining(inDispatcher -> CsThreadFactory.getInstance().newThread(inDispatcher).start());
        this.outDispatchers.iter().forEachRemaining(outDispatcher -> CsThreadFactory.getInstance().newThread(outDispatcher).start());
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

    private RemoteChannelConfig getRemoteChannelConfig(String serviceId) {
        return this.remoteChannelConfig;
    }


    @Override
    public void close() throws IOException {
        closeAll(remotePeerMap.values().iterator());
        closeAll(outDispatchers.iter());
        closeAll(inDispatchers.iter());
        serviceFinder.close();
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

    /**
     * create a new RemotePeer by service id, then start it.
     * The function will obtain the remote address by ServiceFinder and connect it.
     * @param serviceId service id
     * @return RemotePeer
     * @see ServiceFinder
     * @see RemotePeer
     */
    private RemotePeer createAndStartRemotePeer(String serviceId) {
        InetSocketAddress isa = this.serviceFinder.getServiceAddress(serviceId);
        if (isa == null) {
            return null;
        }
        try {
            IChannel iChannel = RemoteChannelFactory.newInstance(serviceId, isa,this.getRemoteChannelConfig(serviceId));
            if (iChannel == null) {
                return null;
            }
            DefaultRemotePeerImpl remotePeer = new DefaultRemotePeerImpl(serviceId, iChannel);
            CsThreadFactory.getInstance().newThread(remotePeer).start();
            this.remotePeerMap.put(serviceId, remotePeer);
            return remotePeer;
        } catch (InterruptedException e) {
            LOG.error("create remote peer occur error", e);
            return null;
        }

    }

    /**
     * Use the class to manage uniformly login and logout of user.
     */
    class UserStateSupervisor {
        private MsgManager self = MsgManager.this;

        public boolean isLocal(String toId) {
            return self.userChannelIdsSet.contains(toId);
        }

        public void login(String uid, Channel channel) {
            IChannel userChannel = new UserChannel(self.curServiceId, channel);
            self.serviceFinder.registerClient(self.curServiceId, uid);
            self.userChannelMap.put(uid, userChannel);
            LOG.info("User: {} login.", uid);
        }

        public void logout(String uid) {
            self.serviceFinder.unregisterClient(self.curServiceId, uid);
            self.userChannelMap.remove(uid);
            LOG.info("User: {} logout.", uid);
        }
    }

    class RemotePeerStateSupervisor {

        private final MsgManager self = MsgManager.this;

        public void connected(String sid, Channel channel) {
            self.remotePeerMap.put(sid, new DefaultRemotePeerImpl(sid, new RemoteChannel(sid, channel)));
            LOG.info("Service: {} connected.", sid);
        }

        public void disconnected(String sid) {
            RemotePeer peer = self.remotePeerMap.remove(sid);
            if (peer != null) {
                try {
                    peer.close();
                } catch (IOException e) {
                    LOG.error("RemotePeer occur exception when close it.", e);
                }
            }
            LOG.info("Service: {} disconnected.", sid);
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
            if (!self.userStateSupervisor.isLocal(toId)) {
                self.nextOut(protocol);
                return;
            }
            IChannel userChannel = self.userChannelMap.get(toId);
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
            if (MsgManager.this.userStateSupervisor.isLocal(toId)) {
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



    @ChannelHandler.Sharable
    static
    class UnifiedHeartbeatHandler extends ChannelInboundHandlerAdapter implements NamedChannelHandler {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                if (IdleState.WRITER_IDLE.equals(((IdleStateEvent) evt).state())) {
                    /*
                    Towards the user channel, the WRITE_IDLE event should be handled by the user.
                    Towards the remote channel, the WRITE_IDLE event should be handled by all remote peer.
                    That maybe set different value of writeIdleTime to accomplish it.
                    */
                    ctx.writeAndFlush(ProtocolUtil.newHeartbeat());
                } else if (IdleState.READER_IDLE.equals(((IdleStateEvent) evt).state())) {
                    LOG.info("The channel unread too many time. We will close it.");
                    ctx.close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public String name() {
            return "unified-heartbeat-handler-shared";
        }
    }

    class RemoteChannelNotificationHandler extends ChannelInboundHandlerAdapter implements NamedChannelHandler {
        private final MsgManager self;
        private String serviceId;
        public RemoteChannelNotificationHandler() {
            this.self = MsgManager.this;
            this.serviceId = null;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(ProtocolUtil.newRegisterNotification(ServerProto.SInner.getDefaultInstance(), self.curServiceId));
            super.handlerAdded(ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ServerProto.SInner) {
                ServerProto.SInner inner = (ServerProto.SInner) msg;
                if (ClientProto.DataType.NOTIFICATION.equals(inner.getType())) {
                    handleNotification(ctx, inner);
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            if (this.serviceId != null) {
                self.remotePeerStateSupervisor.disconnected(this.serviceId);
            }
            super.handlerRemoved(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOG.error("remote channel occur exception.", cause);
            /* close the channel */
            ctx.close();
            super.exceptionCaught(ctx, cause);
        }

        private void handleNotification(ChannelHandlerContext ctx, ServerProto.SInner sInner) {
            int code = sInner.getNotification().getCode();
            String data = sInner.getNotification().getMsg();
            if (Constants.REPLY_PEER_REGISTER == code) {
                if (this.serviceId == null) {
                    /* unregistered */
                    this.serviceId = data;
                    self.remotePeerStateSupervisor.connected(this.serviceId, ctx.channel());
                } else if (this.serviceId.equals(data)) {
                    LOG.info("the service has multiple registration. We will ignore the reply for registration.");
                } else {
                    LOG.error("the service has wrong state. We will close the remote channel");
                    ctx.close();
                }
            }
        }
        @Override
        public String name() {
            return String.format("remote-channel-notification-handler-%s", this.serviceId);
        }
    }

    @ChannelHandler.Sharable
    class RemoteChannelMsgHandler extends ChannelInboundHandlerAdapter implements NamedChannelHandler {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof ServerProto.SInner) {
                ServerProto.SInner sInner = (ServerProto.SInner) msg;
                if (ClientProto.DataType.MSG.equals(sInner.getType())) {
                    ClientProto.CInner cInner = ProtocolUtil.s2c((ServerProto.SInner) msg);
                    MsgManager.this.nextIn(cInner);
                } else {
                    ctx.fireChannelRead(msg);
                }
            }
        }

        @Override
        public String name() {
            return "remote-channel-msg-handler-shared";
        }
    }



    class UserChannelNotificationHandler extends ChannelInboundHandlerAdapter implements NamedChannelHandler {
        private boolean isRegister;
        private final MsgManager self;
        private String clientId = null;
        public UserChannelNotificationHandler() {
            this.isRegister = false;
            this.self = MsgManager.this;
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (this.isRegister) {
                ctx.fireChannelRead(msg);
            } else {
                if (msg instanceof ClientProto.CInner) {
                    ClientProto.CInner inner = (ClientProto.CInner) msg;
                    if (ClientProto.DataType.NOTIFICATION.equals(inner.getType())) {
                        handlerNotification(ctx, inner);
                    } else {
                        ctx.fireChannelRead(msg);
                    }
                } else {
                    LOG.info("unknown type {}", msg.getClass().getName());
                }
                if (!this.isRegister) {
                    sendRegisterNotification(ctx);
                }
            }
        }

        private void handlerNotification(ChannelHandlerContext ctx, ClientProto.CInner inner) {
            ClientProto.Notification notification = inner.getNotification();
            if (notification.getCode() == Constants.REPLY_PEER_REGISTER && !this.isRegister) {
                // register the channel
                this.clientId = notification.getMsg();;
                self.userStateSupervisor.login(this.clientId, ctx.channel());
                this.isRegister = true;
            }
        }

        private void sendRegisterNotification(ChannelHandlerContext ctx) {
            ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
            builder.setType(ClientProto.DataType.NOTIFICATION)
                    .setNotification(ClientProto.Notification.newBuilder().setCode(Constants.NOTE_PEER_REGISTER).build());
            ctx.writeAndFlush(builder.build());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            self.userStateSupervisor.logout(this.clientId);
            super.handlerRemoved(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            LOG.error("user channelHandler occur exception.", cause);
            /* close the channel when the channel occur exception */
            ctx.close();
        }

        @Override
        public String name() {
            return String.format("user-channel-notification-handler-%s", this.clientId);
        }
    }

    @ChannelHandler.Sharable
    class UserChannelMsgHandler extends ChannelInboundHandlerAdapter implements NamedChannelHandler {
        private final MsgManager self;
        public UserChannelMsgHandler() {
            this.self = MsgManager.this;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ClientProto.CInner inner = (ClientProto.CInner) msg;
            if (inner.getType() == ClientProto.DataType.MSG) {
                self.nextOut(inner);
            } else {
                ctx.fireChannelRead(msg);
            }
        }
        @Override
        public String name() {
            return "user-channel-msg-handler-shared";
        }
    }
}
