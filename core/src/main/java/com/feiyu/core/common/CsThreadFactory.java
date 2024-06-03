package com.feiyu.core.common;

import java.util.concurrent.ThreadFactory;

/**
 * @author zhufeifei 2023/9/9
 **/

public class CsThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private CsThreadFactory(){
        this.group = new ThreadGroup("chat-server");
    }
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(this.group, r);
    }

    public static CsThreadFactory getInstance() {
        return Inner.INSTANCE.INS;
    }

    enum Inner {
        INSTANCE;
        private final CsThreadFactory INS = new CsThreadFactory();
    }
}
