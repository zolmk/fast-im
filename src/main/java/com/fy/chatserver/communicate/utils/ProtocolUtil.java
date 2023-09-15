package com.fy.chatserver.communicate.utils;

import com.fy.chatserver.communicate.Constants;
import com.fy.chatserver.communicate.proto.ClientProto;
import com.fy.chatserver.communicate.proto.ServerProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

/**
 * utils for Protobuf
 * @author zhufeifei 2023/9/9
 **/

public class ProtocolUtil {
    /**
     * convert ClientProto.CInner to ClientProto.SInner
     * @param protocol ClientProto.CInner
     * @param serviceId service id
     * @return ServerProto.SInner
     */
    public static ServerProto.SInner c2s(ClientProto.CInner protocol, String serviceId) {
        ServerProto.SInner.Builder builder = ServerProto.SInner.newBuilder();
        builder.setType(protocol.getType());
        builder.setAck(protocol.getAck());
        if (protocol.hasMsg()) {
            builder.setMsg(ServerProto.ServerMsg.newBuilder().setMsg(protocol.getMsg()).setServerId(serviceId).build());
        }
        else if (protocol.hasHeartbeat()) {
            builder.setHeartbeat(protocol.getHeartbeat());
        }
        else if (protocol.hasNotification()) {
            builder.setNotification(protocol.getNotification());
        }
        return builder.build();
    }

    /**
     * Convert ServerProto.SInner to ClientProto.CInner.
     * @param proto ServerProto.SInner
     * @return ClientProto.CInner
     */
    public static ClientProto.CInner s2c(ServerProto.SInner proto) {
        ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
        builder.setAck(proto.getAck());
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

    /**
     * Return a new notification of registration.
     * @param lite the default messageLite instance
     * @param sid the self id
     * @return a MessageLite Object that has same typed with lite parameter.
     */
    public static MessageLite newRegisterNotification(MessageLite lite, String sid) {
        ClientProto.Notification notification = ClientProto.Notification.newBuilder().setCode(Constants.NotificationCode.REPLY_PEER_REGISTER).setMsg(sid).build();
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

    /**
     * Return a new heartbeat object.
     * @param messageLite type for return.
     * @return MessageLite
     */
    public static MessageLite newHeartbeat(MessageLite messageLite) {
        ClientProto.Heartbeat heartbeat = ClientProto.Heartbeat.newBuilder().setData(ByteString.empty()).build();
        if (messageLite instanceof ClientProto.CInner) {
            return ClientProto.CInner.newBuilder().setType(ClientProto.DataType.HEARTBEAT).setAck(0).setHeartbeat(heartbeat).build();
        } else if (messageLite instanceof ServerProto.SInner){
            return ServerProto.SInner.newBuilder().setType(ClientProto.DataType.HEARTBEAT).setAck(0).setHeartbeat(heartbeat).build();
        }
        return null;
    }

    /**
     * Return a new notification
     * @param ack user's ack
     * @param code return code
     * @param msg message
     * @return Notification
     */
    public static MessageLite newUserNotification(long ack, int code, String msg) {
        ClientProto.CInner.Builder builder = ClientProto.CInner.newBuilder();
        builder.setType(ClientProto.DataType.NOTIFICATION);
        builder.setAck(ack);
        builder.setNotification(ClientProto.Notification.newBuilder().setCode(code).setMsg(msg).build());
        return builder.build();
    }
}
