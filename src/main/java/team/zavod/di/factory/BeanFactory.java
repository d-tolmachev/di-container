package team.zavod.di.factory;

import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;

public interface BeanFactory extends BeanDefinitionRegistry {
  <T> T getBean(Class<T> requiredType) throws BeanException;

  <T> T getBean(String name, Class<T> requiredType) throws BeanException;

  <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

  boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

  boolean isPrototype(String name) throws NoSuchBeanDefinitionException;
}
