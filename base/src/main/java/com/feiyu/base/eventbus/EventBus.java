package com.feiyu.base.eventbus;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 事件总线
 * 用于模块间解耦
 */
@Slf4j
public class EventBus {
  private static Map<Class<?>, Set<MethodCaller>> methodCallers = new ConcurrentHashMap<>();

  private static final ExecutorService executorService = new ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors(), 10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), new ThreadFactory() {
    AtomicInteger cnt = new AtomicInteger(0);
    @Override
    public Thread newThread(@NonNull Runnable r) {
      Thread t = new Thread(r);
      t.setDaemon(true);
      t.setName("EventBus-Thread-" + cnt.getAndIncrement());
      return t;
    }
  }, new ThreadPoolExecutor.CallerRunsPolicy());

  /**
   * 注册事件
   * @param object
   */
  public static void register(Object object) {
    final Class<?> clazz = object.getClass();
    List<Method> methods = getMethods(clazz);
    for (Method method : methods) {
      final Class<?> parameterType = method.getParameterTypes()[0];
      if ( ! methodCallers.containsKey(parameterType)) {
        synchronized (parameterType) {
          if ( ! methodCallers.containsKey(parameterType)) {
            methodCallers.put(parameterType, new ConcurrentSkipListSet<>());
          }
        }
      }
      methodCallers.get(parameterType).add(new MethodCaller(method, object));
    }

  }

  /**
   * 取消注册事件
   * @param object
   */
  public static void unregister(Object object) {
    final Class<?> clazz = object.getClass();
    List<Method> methods = getMethods(clazz);
    for (Method method : methods) {
      final Class<?> parameterType = method.getParameterTypes()[0];
      if(methodCallers.containsKey(parameterType)) {
        methodCallers.get(parameterType).remove(new MethodCaller(method, object));
      }
    }
  }

  /**
   * 分发事件
   * @param event e
   */
  public static void post(Object event) {
    Class<?> aClass = event.getClass();
    Set<MethodCaller> methods = methodCallers.get(aClass);
    if (methods == null || methods.isEmpty()) {
      return;
    }
    for (MethodCaller methodCaller : methods) {
      methodCaller.call(event);
    }
  }

  private static List<Method> getMethods(Class<?> clazz) {
    List<Method> list = new ArrayList<>();
    Method[] declaredMethods = clazz.getDeclaredMethods();
    for (Method method : declaredMethods) {
      if (method.isAnnotationPresent(Subscribe.class)) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
          // 参数不匹配
          continue;
        }
        list.add(method);
      }
    }
    return list;
  }

  static class MethodCaller implements Comparable<MethodCaller> {
    private Method method;
    private Object target;
    public MethodCaller(Method method, Object target) {
      this.method = method;
      this.target = target;
    }
    public void call(Object event) {
      Subscribe subscribe = method.getAnnotation(Subscribe.class);
      if (subscribe.async()) {
        executorService.submit(()->{call0(event);});
      } else {
        call0(event);
      }
    }
    public void call0(Object event) {
      try {
        method.invoke(this.target, event);
      } catch (IllegalAccessException | InvocationTargetException e) {
        EventBus.log.error(e.getMessage(), e);
      }
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof MethodCaller) {
        MethodCaller caller = (MethodCaller) obj;
        return this.method.equals(caller.method);
      } else {
        return false;
      }
    }

    @Override
    public int hashCode() {
      return method.hashCode();
    }

    @Override
    public int compareTo(MethodCaller o) {
      return this.hashCode() - o.hashCode();
    }
  }
}
