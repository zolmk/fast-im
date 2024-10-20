package com.feiyu.core.service.impl;

import com.feiyu.base.proto.Messages;
import com.feiyu.core.service.MsgIdentifierService;
import com.feiyu.core.service.MsgInternalHandleService;
import com.feiyu.core.util.ConvertUtil;
import com.feiyu.interfaces.idl.*;
import com.feiyu.core.service.IMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.CompletableFuture;

@Slf4j
@DubboService
public class MessageHandleService implements IMessageHandleService, MsgInternalHandleService {

  @DubboReference
  private IMessageRouteService messageRouteService;
  private final MsgIdentifierService identifierService;
  private final IMsgService iMsgService;

  public MessageHandleService(MsgIdentifierService msgIdentifierService, IMsgService iMsgService) {
    this.identifierService = msgIdentifierService;
    this.iMsgService = iMsgService;
  }

  @Override
  public MsgHandleRsp handle(MsgHandleReq req) {
    log.info("handle message {}", req);
    if (req.getTo() <= 0) {
      // 未知，暂不做处理
      return MsgHandleRsp.newBuilder().setSeq(-1).setMsgId(-1).build();
    }
    Messages.Msg msg = req.getMsg();
    MsgHandleRsp rsp = null;
     switch (msg.getType()) {
      case CONTROL: {
        rsp = handleControl(req);
      } break;
      case GENERIC: {
        rsp = handleGeneric(req);
      } break;
      case NOTICE: {
        rsp = handleNotice(req);
      } break;
      case QUERY: {
        rsp = handleQuery(req);
      } break;
      default: {
        rsp = MsgHandleRsp.getDefaultInstance();
      }
    }
    return rsp;
  }

  @Override
  public CompletableFuture<MsgHandleRsp> handleAsync(MsgHandleReq req) {
    log.info("handleAsync message {}", req);
    return CompletableFuture.supplyAsync(()->handle(req));
  }

  @Override
  public MsgHandleRsp handleNotice(MsgHandleReq req) {
    // 通知消息，直接转发即可
    RouteReq routeReq = RouteReq.newBuilder().setMsg(req.getMsg()).setTo(req.getTo()).build();
    RouteRsp routeRsp = messageRouteService.route(routeReq);
    if (routeRsp.getCode() != 200) {
      return MsgHandleRsp.newBuilder().setMsgId(0L).setSeq(0L).build();
    }
    return MsgHandleRsp.getDefaultInstance();
  }

  @Override
  public MsgHandleRsp handleControl(MsgHandleReq req) {
    // 控制消息，忽略
    Messages.Msg msg = req.getMsg();
    Messages.ControlMsg controlMsg = msg.getControlMsg();
    if (Messages.ControlType.MSG_DELIVERED_FAIL.equals(controlMsg.getType())) {
      log.error("message delivered fail. msg id {}", controlMsg.getMsgId());
    }
    return MsgHandleRsp.getDefaultInstance();
  }

  @Override
  public MsgHandleRsp handleGeneric(MsgHandleReq req) {
    long msgId = identifierService.newId();
    long seq = identifierService.newSeq(req.getTo());

    // 保存消息
    iMsgService.save(ConvertUtil.toEntityMsg(req.getMsg()));

    Messages.Msg msg = setMsgIdAndSeq(req.getMsg(), msgId, seq);
    RouteReq routeReq = RouteReq.newBuilder().setMsg(msg).setTo(req.getTo()).build();
    RouteRsp route = messageRouteService.route(routeReq);
    if (route.getCode() != 200) {
      log.info("route message failed");
      // do something
    }
    return MsgHandleRsp.newBuilder().setMsgId(msgId).setSeq(seq).build();
  }

  @Override
  public MsgHandleRsp handleQuery(MsgHandleReq req) {
    return null;
  }


  private Messages.Msg setMsgIdAndSeq(Messages.Msg msg, long msgId, long seq) {
    Messages.MsgExtraInfo extraInfo = msg.getGenericMsg().getExtraInfo().toBuilder().setMsgId(msgId).setSeq(seq).build();
    Messages.GenericMsg newGenericMsg = msg.getGenericMsg().toBuilder().setExtraInfo(extraInfo).build();
    msg = msg.toBuilder().setGenericMsg(newGenericMsg).build();
    return msg;
  }
}
