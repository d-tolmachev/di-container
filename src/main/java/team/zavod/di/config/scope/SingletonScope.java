package team.zavod.di.config.scope;

import team.zavod.di.factory.registry.DefaultSingletonBeanRegistry;
import team.zavod.di.factory.ObjectProvider;
import team.zavod.di.factory.registry.SingletonBeanRegistry;

public class SingletonScope implements Scope {
  private static final String SCOPE_NAME = "singleton";
  private final SingletonBeanRegistry singletonBeanRegistry;

  public SingletonScope() {
    this.singletonBeanRegistry = new DefaultSingletonBeanRegistry();
  }

  @Override
  public boolean contains(String beanName) throws UnsupportedOperationException {
    return this.singletonBeanRegistry.containsSingleton(beanName);
  }

  @Override
  public Object get(String beanName, Class<?> beanType, ObjectProvider<?> beanProvider) {
    if (!this.singletonBeanRegistry.containsSingleton(beanName)) {
      this.singletonBeanRegistry.registerSingleton(beanType, beanName, beanProvider.getObject());
    }
    return this.singletonBeanRegistry.getSingleton(beanName);
  }

  @Override
  public Object remove(String beanName) throws UnsupportedOperationException {
    Object bean = null;
    if (this.singletonBeanRegistry.containsSingleton(beanName)) {
      bean = this.singletonBeanRegistry.getSingleton(beanName);
      this.singletonBeanRegistry.removeSingleton(beanName);
    }
    return bean;
  }

  public static String getName() {
    return SCOPE_NAME;
  }
}
