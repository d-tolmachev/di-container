package team.zavod.di.factory;

import team.zavod.di.config.BeanDefinition;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;

public interface BeanDefinitionRegistry {
  boolean containsBeanDefinition(String beanName);

  void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

  void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

  BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;
}
