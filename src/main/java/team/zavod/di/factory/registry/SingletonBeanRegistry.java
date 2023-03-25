package team.zavod.di.factory.registry;

import java.util.List;
import java.util.Set;
import team.zavod.di.factory.exception.BeanStoreException;
import team.zavod.di.factory.exception.NoSuchBeanException;

public interface SingletonBeanRegistry {
  boolean containsSingleton(Class<?> beanType);

  boolean containsSingleton(String beanName);

  void registerSingleton(Class<?> beanType, String beanName, Object bean) throws BeanStoreException;

  void removeSingleton(String beanName) throws NoSuchBeanException;

  Object getSingleton(Class<?> beanType) throws NoSuchBeanException;

  Object getSingleton(String beanName) throws NoSuchBeanException;

  Set<Object> getSingletons(Class<?> beanType) throws NoSuchBeanException;

  List<String> getSingletonNames();
}
