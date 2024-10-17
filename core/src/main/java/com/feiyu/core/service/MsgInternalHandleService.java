package com.feiyu.core.service;

import com.feiyu.interfaces.idl.MsgHandleReq;
import com.feiyu.interfaces.idl.MsgHandleRsp;

public interface MsgInternalHandleService {
  /**
   * 处理通知消息
   * @param req
   * @return
   */
  MsgHandleRsp handleNotice(MsgHandleReq req);

  /**
   * 处理控制消息
   * @param req
   * @return
   */
  MsgHandleRsp handleControl(MsgHandleReq req);

  /**
   * 处理通用消息
   * @param req
   * @return
   */
  MsgHandleRsp handleGeneric(MsgHandleReq req);

  /**
   * 处理查询消息
   * @param req
   * @return
   */
  MsgHandleRsp handleQuery(MsgHandleReq req);
}
