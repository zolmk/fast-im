package com.feiyu.common;

/**
 * @author zhufeifei 2024/6/6
 **/

public class R {
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_FAIL = -1;

    public static <T> Result<?> success(T data) {
        return new Result<>(CODE_SUCCESS, data);
    }

    public static <T> Result<?> fail(T data) {
        return new Result<>(CODE_FAIL, data);
    }
}
