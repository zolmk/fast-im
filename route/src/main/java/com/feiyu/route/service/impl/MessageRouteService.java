package com.feiyu.route.service.impl;

import com.feiyu.interfaces.idl.IMessageRouteService;
import com.feiyu.interfaces.idl.RouteReq;
import com.feiyu.interfaces.idl.RouteRsp;
import com.feiyu.route.service.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import java.util.concurrent.CompletableFuture;

@Slf4j
@DubboService
public class MessageRouteService implements IMessageRouteService {
  private final MessageProducer producer;

  public MessageRouteService(MessageProducer producer) {
    this.producer = producer;
  }

  @Override
  public RouteRsp route(RouteReq request) {
    log.info("message route service message. {}", request);
    try {
      this.producer.produce(request.getTo(), request.getMsg());
      return RouteRsp.newBuilder().setCode(200).build();
    } catch (Exception e) {
      log.error("message route service error.", e);
    }
    return RouteRsp.newBuilder().setCode(500).build();
  }

  @Override
  public CompletableFuture<RouteRsp> routeAsync(RouteReq request) {
    return CompletableFuture.supplyAsync(() -> route(request));
  }
}
