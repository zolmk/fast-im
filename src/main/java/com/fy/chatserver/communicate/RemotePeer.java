package com.fy.chatserver.communicate;


import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.enums.ComponentStatus;
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
