package com.feiyu.base.interfaces;

/**
 * 生命周期.
 *
 * @author zolmk
 */
public interface LifeCycle {

    void start() throws Exception;

    void destroy() throws Exception;
}
