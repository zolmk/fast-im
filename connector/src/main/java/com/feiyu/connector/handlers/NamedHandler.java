package com.feiyu.connector.handlers;

import com.feiyu.base.interfaces.Named;
import io.netty.channel.ChannelHandler;

/**
 * 基础的ChannelHandler.
 *
 * @author Zhuff
 */
public interface NamedHandler extends ChannelHandler, Named {
}
