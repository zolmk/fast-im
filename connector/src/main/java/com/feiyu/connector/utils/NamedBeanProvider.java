package com.feiyu.connector.utils;

import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(value = 100)
public class NamedBeanProvider implements ApplicationContextAware {

  private static ApplicationContext applicationContext;
  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * 获取单例对象
   * @param clazz
   * @return
   * @param <T>
   */
  public static <T> T getSingleton(Class<T> clazz) {
    return (T) applicationContext.getBean(clazz);
  }
}
