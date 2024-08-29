package com.feiyu.interfaces;

import com.google.protobuf.MessageLite;

public interface IMessageRouteService {
    MessageLite route(MessageLite message);
}
