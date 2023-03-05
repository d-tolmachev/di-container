package team.zavod.di.factory;

import java.util.Set;
import team.zavod.di.factory.exception.BeanStoreException;
import team.zavod.di.factory.exception.NoSuchBeanException;

public interface BeanRegistry {
  boolean containsBean(Class<?> beanType);

  boolean containsBean(String beanName);

  void registerBean(Class<?> beanType, String beanName, Object bean) throws BeanStoreException;

  void removeBean(String beanName) throws NoSuchBeanException;

  Object getBean(Class<?> beanType) throws NoSuchBeanException;

  Object getBean(String beanName) throws NoSuchBeanException;

  Set<Object> getBeans(Class<?> beanType) throws NoSuchBeanException;
}
