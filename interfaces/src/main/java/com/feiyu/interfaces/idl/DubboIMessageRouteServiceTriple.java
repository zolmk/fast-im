/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.feiyu.interfaces.idl;

import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.PathResolver;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.ServerService;
import org.apache.dubbo.rpc.TriRpcStatus;
import org.apache.dubbo.rpc.model.MethodDescriptor;
import org.apache.dubbo.rpc.model.ServiceDescriptor;
import org.apache.dubbo.rpc.model.StubMethodDescriptor;
import org.apache.dubbo.rpc.model.StubServiceDescriptor;
import org.apache.dubbo.rpc.stub.BiStreamMethodHandler;
import org.apache.dubbo.rpc.stub.ServerStreamMethodHandler;
import org.apache.dubbo.rpc.stub.StubInvocationUtil;
import org.apache.dubbo.rpc.stub.StubInvoker;
import org.apache.dubbo.rpc.stub.StubMethodHandler;
import org.apache.dubbo.rpc.stub.StubSuppliers;
import org.apache.dubbo.rpc.stub.UnaryStubMethodHandler;

import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.concurrent.CompletableFuture;

public final class DubboIMessageRouteServiceTriple {

    public static final String SERVICE_NAME = IMessageRouteService.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,IMessageRouteService.class);

    static {
        org.apache.dubbo.rpc.protocol.tri.service.SchemaDescriptorRegistry.addSchemaDescriptor(SERVICE_NAME,Interfaces.getDescriptor());
        StubSuppliers.addSupplier(SERVICE_NAME, DubboIMessageRouteServiceTriple::newStub);
        StubSuppliers.addSupplier(IMessageRouteService.JAVA_SERVICE_NAME,  DubboIMessageRouteServiceTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(IMessageRouteService.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static IMessageRouteService newStub(Invoker<?> invoker) {
        return new IMessageRouteServiceStub((Invoker<IMessageRouteService>)invoker);
    }

    private static final StubMethodDescriptor routeMethod = new StubMethodDescriptor("route",
    com.feiyu.interfaces.idl.RouteReq.class, com.feiyu.interfaces.idl.RouteRsp.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.RouteReq::parseFrom,
    com.feiyu.interfaces.idl.RouteRsp::parseFrom);

    private static final StubMethodDescriptor routeAsyncMethod = new StubMethodDescriptor("route",
    com.feiyu.interfaces.idl.RouteReq.class, java.util.concurrent.CompletableFuture.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.RouteReq::parseFrom,
    com.feiyu.interfaces.idl.RouteRsp::parseFrom);

    private static final StubMethodDescriptor routeProxyAsyncMethod = new StubMethodDescriptor("routeAsync",
    com.feiyu.interfaces.idl.RouteReq.class, com.feiyu.interfaces.idl.RouteRsp.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.RouteReq::parseFrom,
    com.feiyu.interfaces.idl.RouteRsp::parseFrom);




    static{
        serviceDescriptor.addMethod(routeMethod);
        serviceDescriptor.addMethod(routeProxyAsyncMethod);
    }

    public static class IMessageRouteServiceStub implements IMessageRouteService{
        private final Invoker<IMessageRouteService> invoker;

        public IMessageRouteServiceStub(Invoker<IMessageRouteService> invoker) {
            this.invoker = invoker;
        }

        @Override
        public com.feiyu.interfaces.idl.RouteRsp route(com.feiyu.interfaces.idl.RouteReq request){
            return StubInvocationUtil.unaryCall(invoker, routeMethod, request);
        }

        public CompletableFuture<com.feiyu.interfaces.idl.RouteRsp> routeAsync(com.feiyu.interfaces.idl.RouteReq request){
            return StubInvocationUtil.unaryCall(invoker, routeAsyncMethod, request);
        }

        public void route(com.feiyu.interfaces.idl.RouteReq request, StreamObserver<com.feiyu.interfaces.idl.RouteRsp> responseObserver){
            StubInvocationUtil.unaryCall(invoker, routeMethod , request, responseObserver);
        }



    }

    public static abstract class IMessageRouteServiceImplBase implements IMessageRouteService, ServerService<IMessageRouteService> {

        private <T, R> BiConsumer<T, StreamObserver<R>> syncToAsync(java.util.function.Function<T, R> syncFun) {
            return new BiConsumer<T, StreamObserver<R>>() {
                @Override
                public void accept(T t, StreamObserver<R> observer) {
                    try {
                        R ret = syncFun.apply(t);
                        observer.onNext(ret);
                        observer.onCompleted();
                    } catch (Throwable e) {
                        observer.onError(e);
                    }
                }
            };
        }

        @Override
        public CompletableFuture<com.feiyu.interfaces.idl.RouteRsp> routeAsync(com.feiyu.interfaces.idl.RouteReq request){
                return CompletableFuture.completedFuture(route(request));
        }

        /**
        * This server stream type unary method is <b>only</b> used for generated stub to support async unary method.
        * It will not be called if you are NOT using Dubbo3 generated triple stub and <b>DO NOT</b> implement this method.
        */
        public void route(com.feiyu.interfaces.idl.RouteReq request, StreamObserver<com.feiyu.interfaces.idl.RouteRsp> responseObserver){
            routeAsync(request).whenComplete((r, t) -> {
                if (t != null) {
                    responseObserver.onError(t);
                } else {
                    responseObserver.onNext(r);
                    responseObserver.onCompleted();
                }
            });
        }

        @Override
        public final Invoker<IMessageRouteService> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/route");
            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/routeAsync");
            // for compatibility
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/route");
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/routeAsync");


            BiConsumer<com.feiyu.interfaces.idl.RouteReq, StreamObserver<com.feiyu.interfaces.idl.RouteRsp>> routeFunc = this::route;
            handlers.put(routeMethod.getMethodName(), new UnaryStubMethodHandler<>(routeFunc));
            BiConsumer<com.feiyu.interfaces.idl.RouteReq, StreamObserver<com.feiyu.interfaces.idl.RouteRsp>> routeAsyncFunc = syncToAsync(this::route);
            handlers.put(routeProxyAsyncMethod.getMethodName(), new UnaryStubMethodHandler<>(routeAsyncFunc));




            return new StubInvoker<>(this, url, IMessageRouteService.class, handlers);
        }


        @Override
        public com.feiyu.interfaces.idl.RouteRsp route(com.feiyu.interfaces.idl.RouteReq request){
            throw unimplementedMethodException(routeMethod);
        }





        @Override
        public final ServiceDescriptor getServiceDescriptor() {
            return serviceDescriptor;
        }
        private RpcException unimplementedMethodException(StubMethodDescriptor methodDescriptor) {
            return TriRpcStatus.UNIMPLEMENTED.withDescription(String.format("Method %s is unimplemented",
                "/" + serviceDescriptor.getInterfaceName() + "/" + methodDescriptor.getMethodName())).asException();
        }
    }

}
