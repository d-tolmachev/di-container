package team.zavod.di.factory;

import java.util.Collection;
import java.util.List;
import team.zavod.di.config.postprocessor.BeanPostProcessor;
import team.zavod.di.config.scope.Scope;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;

public interface BeanFactory extends BeanDefinitionRegistry {
  boolean containsBean(String name);

  <T> T getBean(Class<T> requiredType) throws BeanException;

  <T> T getBean(String name, Class<T> requiredType) throws BeanException;

  void destroyBean(String name) throws BeanException;

  <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

  <T> ObjectProvider<T> getBeanProvider(String name, Class<T> requiredType);

  boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

  boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

  boolean isThread(String name) throws NoSuchBeanDefinitionException;

  void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

  void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors);

  List<BeanPostProcessor> getBeanPostProcessors();

  void registerScope(String scopeName, Scope scope);

  Scope getScope(String scopeName);

  List<String> getScopeNames();
}
