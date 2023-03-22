package team.zavod.di.context;

import java.util.Collection;
import java.util.List;
import team.zavod.di.config.BeanPostProcessor;
import team.zavod.di.factory.SingletonBeanRegistry;
import team.zavod.di.factory.BeanFactory;

public interface ApplicationContext extends BeanFactory, SingletonBeanRegistry, LifeCycle {
  String getId();

  void setId(String id);

  boolean containsBean(String name);

  void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

  void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors);

  List<BeanPostProcessor> getBeanPostProcessors();
}
