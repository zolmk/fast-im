package com.fy.chatserver.communicate;

import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.google.protobuf.MessageLite;

/**
 * provider protobuf for user and server
 * @author zolmk
 */
public class ProtobufProvider {

    public static MessageLite forUser() {
        return ClientProto.CInner.getDefaultInstance();
    }

    public static MessageLite forServer() {
        return ServerProto.SInner.getDefaultInstance();
    }

}
