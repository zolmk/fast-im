package com.feiyu.ulsequence;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author zhufeifei 2024/6/5
 **/

@Data
@AllArgsConstructor
public class Result <T>{
    private int code;
    private T data;
}
