package com.feiyu.route.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 基于zk的路由配置类
 * @author zhufeifei 2024/6/8
 **/

@Data
@Configuration
public class ZkRouteConfiguration {
    @Value(value = "${fast-im.zk.root-path}")
    private String rootPath;
    @Value(value = "${fast-im.zk.connect-string}")
    private String connectString;
    @Value(value = "${fast-im.node-id}")
    private String nodeId;
    @Value(value = "${fast-im.node-ip}")
    private String nodeIp;
}
