package team.zavod.di.config.scope;

import team.zavod.di.factory.registry.DefaultSingletonBeanRegistry;
import team.zavod.di.factory.ObjectProvider;
import team.zavod.di.factory.registry.SingletonBeanRegistry;

public class ThreadScope implements Scope {
  private static final String SCOPE_NAME = "thread";
  private final ThreadLocal<SingletonBeanRegistry> threadBeanRegistries;

  public ThreadScope() {
    this.threadBeanRegistries = ThreadLocal.withInitial(DefaultSingletonBeanRegistry::new);
  }

  @Override
  public boolean contains(String beanName) throws UnsupportedOperationException {
    SingletonBeanRegistry threadBeanRegistry = this.threadBeanRegistries.get();
    return threadBeanRegistry.containsSingleton(beanName);
  }

  @Override
  public Object get(String beanName, Class<?> beanType, ObjectProvider<?> beanProvider) {
    SingletonBeanRegistry threadBeanRegistry = this.threadBeanRegistries.get();
    if (!threadBeanRegistry.containsSingleton(beanName)) {
      threadBeanRegistry.registerSingleton(beanType, beanName, beanProvider.getObject());
    }
    return threadBeanRegistry.getSingleton(beanName);
  }

  @Override
  public Object remove(String beanName) throws UnsupportedOperationException {
    SingletonBeanRegistry threadBeanRegistry = this.threadBeanRegistries.get();
    Object bean = null;
    if (threadBeanRegistry.containsSingleton(beanName)) {
      bean = threadBeanRegistry.getSingleton(beanName);
      threadBeanRegistry.removeSingleton(beanName);
    }
    return bean;
  }

  public static String getName() {
    return SCOPE_NAME;
  }
}
