package com.fy.chatserver.communicate.utils;

import com.fy.chatserver.communicate.Constants;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.google.protobuf.MessageLite;

/**
 * @author zhufeifei 2023/9/9
 **/

public class ProtocolUtil {
    public static ServerProto.SInner c2s(ClientProto.CInner protocol, String serviceId, long ack) {
        ServerProto.SInner.Builder builder = ServerProto.SInner.newBuilder();
        builder.setType(protocol.getType());
        if (protocol.hasMsg()) {
            builder.setMsg(ServerProto.ServerMsg.newBuilder().setMsg(protocol.getMsg()).setServerId(serviceId).setAck(ack).build());
        }
        else if (protocol.hasHeartbeat()) {
            builder.setHeartbeat(protocol.getHeartbeat());
        }
        else if (protocol.hasNotification()) {
            builder.setNotification(protocol.getNotification());
        }
        return builder.build();
    }

    public static ClientProto.CInner s2c(ServerProto.SInner proto) {
        ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
        builder.setType(proto.getType());
        if (proto.hasMsg()) {
            builder.setMsg(proto.getMsg().getMsg());
        }
        else if (proto.hasHeartbeat()) {
            builder.setHeartbeat(proto.getHeartbeat());
        }
        else if (proto.hasNotification()) {
            builder.setNotification(proto.getNotification());
        }
        return builder.build();
    }

    public static MessageLite registerNotification(MessageLite lite, String sid) {
        ClientProto.Notification notification = ClientProto.Notification.newBuilder().setCode(Constants.REPLY_PEER_REGISTER).setMsg(sid).build();
        if (lite instanceof ServerProto.SInner) {

            ServerProto.SInner.Builder builder = ServerProto.SInner.newBuilder();
            builder.setType(ClientProto.DataType.NOTIFICATION)
                    .setNotification(notification);
            return builder.build();
        } else if (lite instanceof ClientProto.CInner) {
            ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
            builder.setType(ClientProto.DataType.NOTIFICATION)
                    .setNotification(notification);
            return builder.build();
        }
        return null;
    }

}
