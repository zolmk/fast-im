package com.feiyu.base.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * web项目中的返回结果类.
 *
 * @author zhufeifei 2024/6/5
 **/

@Data
@AllArgsConstructor
public class Result<T> implements Serializable {
  private int code;
  private T data;
}
