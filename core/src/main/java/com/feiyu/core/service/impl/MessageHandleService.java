package com.feiyu.core.service.impl;

import com.feiyu.base.proto.Messages;
import com.feiyu.core.service.ClientStatusService;
import com.feiyu.core.service.MsgIdentifierService;
import com.feiyu.core.service.MsgInternalHandleService;
import com.feiyu.core.util.ConvertUtil;
import com.feiyu.interfaces.idl.*;
import com.feiyu.core.service.IMsgService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.CompletableFuture;

import static com.feiyu.base.Constants.*;
import static com.feiyu.base.proto.MsgStatus.*;

@Slf4j
@DubboService
public class MessageHandleService implements IMessageHandleService, MsgInternalHandleService {

  @DubboReference
  private IMessageRouteService messageRouteService;
  private final MsgIdentifierService identifierService;
  private final IMsgService iMsgService;
  private final ClientStatusService clientStatusService;

  public MessageHandleService(MsgIdentifierService msgIdentifierService, IMsgService iMsgService, ClientStatusService clientStatusService) {
    this.identifierService = msgIdentifierService;
    this.iMsgService = iMsgService;
    this.clientStatusService = clientStatusService;
  }

  @Override
  public MsgHandleRsp handle(MsgHandleReq req) {
    log.info("handle message {}", req);
    if (req.getTo() <= 0) {
      // 未知，暂不做处理
      return getMsgHandleRsq(-1);
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
      return getMsgHandleRsq(MSG_FAIL_CODE);
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
      // 更新消息状态
      iMsgService.update()
              .eq("id", controlMsg.getMsgId())
              .set("status", RECEIVED.code())
              .update();
    }
    return MsgHandleRsp.getDefaultInstance();
  }

  @Override
  public MsgHandleRsp handleGeneric(MsgHandleReq req) {
    long msgId = identifierService.newId();
    long seq = identifierService.newSeq(req.getTo());
    boolean saved = true;
    try {
      // 保存消息
      iMsgService.save(ConvertUtil.toEntityMsg(req.getMsg()));
    } catch (Exception e) {
      saved = false;
      log.error("handle generic req error.", e);
    }
    if (!saved) {
      return getMsgHandleRsq(MSG_FAIL_CODE);
    }

    MsgHandleRsp success = getMsgHandleRsq(MSG_SUCCESS_CODE, msgId, seq);

    // 查询用户在线状态
    if (clientStatusService.offline(req.getTo())) {
      // 用户离线，保存消息到离线表 TODO 离线表
      log.info("client {} offline.", req.getTo());

      return success;
    }

    Messages.Msg msg = setMsgIdAndSeq(req.getMsg(), msgId, seq);
    RouteReq routeReq = RouteReq.newBuilder().setMsg(msg).setTo(req.getTo()).build();
    RouteRsp route = messageRouteService.route(routeReq);
    if (route.getCode() == 200) {
      log.info("route message {} success.", msgId);
      // 设置消息已投递
      iMsgService.update().eq("id", msgId).set("status", 1).update();

    } else {
      log.info("route message {} failed.", msgId);
      // do something
    }

    return success;
  }

  @Override
  public MsgHandleRsp handleQuery(MsgHandleReq req) {
    return null;
  }

  /**
   * 为给定消息体设置消息ID和序列号并返回
   * @param msg
   * @param msgId
   * @param seq
   * @return
   */
  private Messages.Msg setMsgIdAndSeq(Messages.Msg msg, long msgId, long seq) {
    Messages.MsgExtraInfo extraInfo = msg.getGenericMsg().getExtraInfo().toBuilder().setMsgId(msgId).setSeq(seq).build();
    Messages.GenericMsg newGenericMsg = msg.getGenericMsg().toBuilder().setExtraInfo(extraInfo).build();
    msg = msg.toBuilder().setGenericMsg(newGenericMsg).build();
    return msg;
  }

  private MsgHandleRsp getMsgHandleRsq(int code) {
    return MsgHandleRsp.newBuilder().setCode(code).build();
  }

  private MsgHandleRsp getMsgHandleRsq(int code, long msgId, long seq) {
    GenericMsgHandleResult res = GenericMsgHandleResult.newBuilder()
            .setMsgId(msgId)
            .setSeq(seq)
            .build();
    return MsgHandleRsp.newBuilder().setCode(code).setRes(res).build();
  }
}
