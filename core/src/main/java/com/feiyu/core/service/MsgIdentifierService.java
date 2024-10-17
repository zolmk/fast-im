package com.feiyu.core.service;

/**
 * 消息ID和序列号service
 */
public interface MsgIdentifierService {
  /**
   * 新生成一个id
   * @return
   */
  long newId();

  /**
   * 新生成一个序列号
   * @param uid
   * @return
   */
  long newSeq(long uid);
}
