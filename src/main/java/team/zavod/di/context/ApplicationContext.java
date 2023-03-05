package team.zavod.di.context;

import java.util.Collection;
import java.util.List;
import team.zavod.di.config.BeanPostProcessor;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory, LifeCycle {
  String getId();

  void setId(String id);

  boolean containsBean(String name);

  boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

  boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

  void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

  void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors);

  List<BeanPostProcessor> getBeanPostProcessors();
}
