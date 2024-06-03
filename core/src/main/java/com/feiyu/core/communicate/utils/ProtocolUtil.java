package com.feiyu.core.communicate.utils;

import com.feiyu.core.communicate.Constants;
import com.feiyu.core.communicate.proto.ClientProto;
import com.feiyu.core.communicate.proto.ServerProto;
import com.feiyu.core.persistent.entity.UnreadEntity;
import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<ClientProto.CInner> unread2Client(List<UnreadEntity> entities) {
        return entities.stream().map(entity -> {
             return ClientProto.CInner.newBuilder().setType(ClientProto.DataType.MSG)
                     .setMsg(ClientProto.Msg.newBuilder()
                             .setType(ClientProto.MsgType.forNumber(entity.getMsgType()))
                             .setIsGroup(entity.isGroup())
                             .setTo(entity.getToId())
                             .setFrom(entity.getFromId())
                             .setData(ByteString.copyFrom(entity.getData(), StandardCharsets.UTF_8))
                             .setGid(entity.getGid())
                             .setSuffix(entity.getSuffix())
                             .setPrefix(entity.getPrefix())
                             .build()).build();
        }).collect(Collectors.toList());
    }
}
