package com.feiyu.route;

import java.io.Closeable;
import java.net.InetSocketAddress;

/**
 * @author feiyu 2023/9/9
 **/


public interface ServiceFinder extends Closeable {
    /**
     * null if the client is offline
     * @param clientId the client id
     * @return the service id
     */
    String findService(String clientId);

    /**
     * null if the service is offline
     * @param serviceId the service id
     * @return isa
     */
    InetSocketAddress getServiceAddress(String serviceId);

    /**
     * true if the service is alive
     * @param serviceId the service id
     * @return boolean
     */
    boolean keepalive(String serviceId);

    /**
     * register the client to service
     * @param clientId client id
     */
    void registerClient(String clientId);

    /**
     * unregister the client to service
     * @param clientId client id
     */
    void unregisterClient(String clientId);

    /**
     * register service
     */
    void registerServer();

    /**
     * unregister service
     * @param serviceId service id
     */
    void unregisterServer(String serviceId);
}
