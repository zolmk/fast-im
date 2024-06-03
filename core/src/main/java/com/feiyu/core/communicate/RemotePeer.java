package com.feiyu.core.communicate;


import com.feiyu.core.enums.ComponentStatus;
import com.google.protobuf.MessageLite;
import io.netty.util.concurrent.Future;

import java.io.Closeable;

/**
 * @author zhufeifei 2023/9/8
 **/


public interface RemotePeer extends Runnable, Closeable  {
    String serviceId();
    Future<?> write(MessageLite protocol);
    IChannel channel();
    ComponentStatus status();
}
