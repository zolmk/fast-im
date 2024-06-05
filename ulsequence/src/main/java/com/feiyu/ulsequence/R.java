package com.feiyu.ulsequence;

/**
 * @author zhufeifei 2024/6/6
 **/

public class R {
    public static <T> Result<?> success(T data) {
        return new Result<>(200, data);
    }

    public static <T> Result<?> fail(T data) {
        return new Result<>(-1, data);
    }
}
