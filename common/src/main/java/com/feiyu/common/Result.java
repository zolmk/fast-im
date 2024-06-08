package com.feiyu.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * web项目中的返回结果类
 * @author zhufeifei 2024/6/5
 **/

@Data
@AllArgsConstructor
public class Result <T>{
    private int code;
    private T data;
}
