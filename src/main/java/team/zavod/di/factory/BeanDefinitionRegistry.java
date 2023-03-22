package team.zavod.di.factory;

import java.util.Set;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;

public interface BeanDefinitionRegistry {
  boolean containsBeanDefinition(String bean);

  void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

  void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

  BeanDefinition getBeanDefinition(String bean) throws NoSuchBeanDefinitionException;

  Set<BeanDefinition> getBeanDefinitions(String beanClassName) throws NoSuchBeanDefinitionException;
}
