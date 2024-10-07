package com.feiyu.connector.utils;

import io.netty.util.internal.ObjectUtil;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class NamedBeanProvider implements ApplicationContextAware {
  /**
   * 提供对应类得前缀匹配对象
   * @param prefixName 前缀
   * @param clazz 类型
   * @return
   */
  public <T> T matchPrefix(String prefixName, Class<T> clazz) {
    prefixName = prefixName.toLowerCase(Locale.ROOT);
    T bean = null;
    String[] beanNamesForType = applicationContext.getBeanNamesForType(clazz);
    for(String beanName : beanNamesForType) {
      if (beanName.startsWith(prefixName)) {
        bean = applicationContext.getBean(beanName, clazz);
      }
    }
    return ObjectUtil.checkNotNull(bean, "No MessageReceiver found for " + prefixName);
  }

  private ApplicationContext applicationContext;
  @Override
  public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
