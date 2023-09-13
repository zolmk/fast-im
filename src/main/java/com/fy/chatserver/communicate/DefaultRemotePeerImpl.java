package com.fy.chatserver.communicate;

import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.fy.chatserver.communicate.utils.ProtocolUtil;
import com.fy.chatserver.enums.ComponentStatus;
import com.google.protobuf.MessageLite;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhufeifei 2023/9/9
 **/

public class DefaultRemotePeerImpl implements RemotePeer {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRemotePeerImpl.class);
    private final LinkedBlockingQueue<MessageLite> queue;
    private final ConcurrentMap<Integer, Future<?>> awaitFutureMap;
    private final IChannel remoteChannel;
    private final String serviceId;
    private final AtomicBoolean isRunning;
    private volatile ComponentStatus status;
    private Thread executeThread;

    public DefaultRemotePeerImpl(String serviceId, IChannel remoteChannel) {
        this.serviceId = serviceId;
        this.queue = new LinkedBlockingQueue<>();
        this.remoteChannel = remoteChannel;
        this.awaitFutureMap = new ConcurrentHashMap<>();
        this.isRunning = new AtomicBoolean(true);
        this.status = ComponentStatus.NOT_START;
    }

    @Override
    public String serviceId() {
        return this.serviceId;
    }

    @Override
    public Future<?> write(MessageLite protocol) {
        if (this.status != ComponentStatus.RUNNING) {
            return new FailedFuture<>(GlobalEventExecutor.INSTANCE, new Throwable("The remote peer not start."));
        }
        if (queue.offer(protocol)) {
            int hashcode = protocol.hashCode();
            Future<?> future = new DefaultPromise<>(GlobalEventExecutor.INSTANCE);
            this.awaitFutureMap.put(hashcode, future);
            return future;
        }
        return new FailedFuture<>(GlobalEventExecutor.INSTANCE, new Throwable("the offer op of queue failed."));
    }

    @Override
    public IChannel channel() {
        return this.remoteChannel;
    }

    @Override
    public ComponentStatus status() {
        return this.status;
    }

    @Override
    public void run() {
        this.executeThread = Thread.currentThread();
        this.executeThread.setName(String.format("remote-peer-thread-%s", this.serviceId));
        int hashcode = 0;
        MessageLite protocol = null;
        this.status = ComponentStatus.RUNNING;
        while (this.isRunning.get()) {
            try {
                protocol = this.queue.poll(1000, TimeUnit.MILLISECONDS);
                if (protocol == null) {
                    continue;
                }
                hashcode = protocol.hashCode();
                MessageLite serverProtocol = ProtocolUtil.c2s((ClientProto.CInner) protocol, this.serviceId, 0L);
                if (remoteChannel.isWritable()) {
                    int finalHashcode = hashcode;
                    this.remoteChannel.writeAndFlush(serverProtocol).addListener(future -> {
                        DefaultPromise<?> awaitFuture = (DefaultPromise<?>) this.awaitFutureMap.get(finalHashcode);
                        if (future.isSuccess()) {
                            awaitFuture.setSuccess(null);
                        } else {
                            awaitFuture.setFailure(future.cause());
                        }
                    });
                } else {
                    this.queue.offer(protocol);
                    Thread.sleep(100);
                    LOG.info("remote channel not write.");
                }
            } catch (InterruptedException e) {
                LOG.error("{} interrupted.", this.executeThread.getName());
            }

        }
    }

    @Override
    public void close() throws IOException {
        if (this.status == ComponentStatus.CLOSED) {
            return;
        }
        this.isRunning.set(false);
        this.status = ComponentStatus.CLOSED;
        this.queue.clear();
        this.remoteChannel.close();
        this.awaitFutureMap.clear();
    }
}
