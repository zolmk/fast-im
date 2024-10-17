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

public final class DubboISequenceServiceTriple {

    public static final String SERVICE_NAME = ISequenceService.SERVICE_NAME;

    private static final StubServiceDescriptor serviceDescriptor = new StubServiceDescriptor(SERVICE_NAME,ISequenceService.class);

    static {
        org.apache.dubbo.rpc.protocol.tri.service.SchemaDescriptorRegistry.addSchemaDescriptor(SERVICE_NAME,Interfaces.getDescriptor());
        StubSuppliers.addSupplier(SERVICE_NAME, DubboISequenceServiceTriple::newStub);
        StubSuppliers.addSupplier(ISequenceService.JAVA_SERVICE_NAME,  DubboISequenceServiceTriple::newStub);
        StubSuppliers.addDescriptor(SERVICE_NAME, serviceDescriptor);
        StubSuppliers.addDescriptor(ISequenceService.JAVA_SERVICE_NAME, serviceDescriptor);
    }

    @SuppressWarnings("all")
    public static ISequenceService newStub(Invoker<?> invoker) {
        return new ISequenceServiceStub((Invoker<ISequenceService>)invoker);
    }

    private static final StubMethodDescriptor genMethod = new StubMethodDescriptor("gen",
    com.feiyu.interfaces.idl.SequenceReq.class, com.feiyu.interfaces.idl.SequenceRsp.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.SequenceReq::parseFrom,
    com.feiyu.interfaces.idl.SequenceRsp::parseFrom);

    private static final StubMethodDescriptor genAsyncMethod = new StubMethodDescriptor("gen",
    com.feiyu.interfaces.idl.SequenceReq.class, java.util.concurrent.CompletableFuture.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.SequenceReq::parseFrom,
    com.feiyu.interfaces.idl.SequenceRsp::parseFrom);

    private static final StubMethodDescriptor genProxyAsyncMethod = new StubMethodDescriptor("genAsync",
    com.feiyu.interfaces.idl.SequenceReq.class, com.feiyu.interfaces.idl.SequenceRsp.class, MethodDescriptor.RpcType.UNARY,
    obj -> ((Message) obj).toByteArray(), obj -> ((Message) obj).toByteArray(), com.feiyu.interfaces.idl.SequenceReq::parseFrom,
    com.feiyu.interfaces.idl.SequenceRsp::parseFrom);




    static{
        serviceDescriptor.addMethod(genMethod);
        serviceDescriptor.addMethod(genProxyAsyncMethod);
    }

    public static class ISequenceServiceStub implements ISequenceService{
        private final Invoker<ISequenceService> invoker;

        public ISequenceServiceStub(Invoker<ISequenceService> invoker) {
            this.invoker = invoker;
        }

        @Override
        public com.feiyu.interfaces.idl.SequenceRsp gen(com.feiyu.interfaces.idl.SequenceReq request){
            return StubInvocationUtil.unaryCall(invoker, genMethod, request);
        }

        public CompletableFuture<com.feiyu.interfaces.idl.SequenceRsp> genAsync(com.feiyu.interfaces.idl.SequenceReq request){
            return StubInvocationUtil.unaryCall(invoker, genAsyncMethod, request);
        }

        public void gen(com.feiyu.interfaces.idl.SequenceReq request, StreamObserver<com.feiyu.interfaces.idl.SequenceRsp> responseObserver){
            StubInvocationUtil.unaryCall(invoker, genMethod , request, responseObserver);
        }



    }

    public static abstract class ISequenceServiceImplBase implements ISequenceService, ServerService<ISequenceService> {

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
        public CompletableFuture<com.feiyu.interfaces.idl.SequenceRsp> genAsync(com.feiyu.interfaces.idl.SequenceReq request){
                return CompletableFuture.completedFuture(gen(request));
        }

        /**
        * This server stream type unary method is <b>only</b> used for generated stub to support async unary method.
        * It will not be called if you are NOT using Dubbo3 generated triple stub and <b>DO NOT</b> implement this method.
        */
        public void gen(com.feiyu.interfaces.idl.SequenceReq request, StreamObserver<com.feiyu.interfaces.idl.SequenceRsp> responseObserver){
            genAsync(request).whenComplete((r, t) -> {
                if (t != null) {
                    responseObserver.onError(t);
                } else {
                    responseObserver.onNext(r);
                    responseObserver.onCompleted();
                }
            });
        }

        @Override
        public final Invoker<ISequenceService> getInvoker(URL url) {
            PathResolver pathResolver = url.getOrDefaultFrameworkModel()
            .getExtensionLoader(PathResolver.class)
            .getDefaultExtension();
            Map<String,StubMethodHandler<?, ?>> handlers = new HashMap<>();

            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/gen");
            pathResolver.addNativeStub( "/" + SERVICE_NAME + "/genAsync");
            // for compatibility
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/gen");
            pathResolver.addNativeStub( "/" + JAVA_SERVICE_NAME + "/genAsync");


            BiConsumer<com.feiyu.interfaces.idl.SequenceReq, StreamObserver<com.feiyu.interfaces.idl.SequenceRsp>> genFunc = this::gen;
            handlers.put(genMethod.getMethodName(), new UnaryStubMethodHandler<>(genFunc));
            BiConsumer<com.feiyu.interfaces.idl.SequenceReq, StreamObserver<com.feiyu.interfaces.idl.SequenceRsp>> genAsyncFunc = syncToAsync(this::gen);
            handlers.put(genProxyAsyncMethod.getMethodName(), new UnaryStubMethodHandler<>(genAsyncFunc));




            return new StubInvoker<>(this, url, ISequenceService.class, handlers);
        }


        @Override
        public com.feiyu.interfaces.idl.SequenceRsp gen(com.feiyu.interfaces.idl.SequenceReq request){
            throw unimplementedMethodException(genMethod);
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
