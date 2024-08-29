package com.feiyu.core.handler;

import com.feiyu.interfaces.IMessageHandleService;
import com.google.protobuf.MessageLite;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService(interfaceClass = IMessageHandleService.class, loadbalance = "random")
public class GenericMessageHandleService implements IMessageHandleService {
    private static final Logger log = LoggerFactory.getLogger(GenericMessageHandleService.class);

    @Override
    public void handle(MessageLite message) {
        log.info("handle message: {}", message);
    }
}
