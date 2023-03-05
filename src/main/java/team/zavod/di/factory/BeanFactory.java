package team.zavod.di.factory;

import team.zavod.di.exception.BeanException;

public interface BeanFactory extends BeanDefinitionRegistry {
  <T> T getBean(Class<T> requiredType) throws BeanException;

  <T> T getBean(String name, Class<T> requiredType) throws BeanException;
}
