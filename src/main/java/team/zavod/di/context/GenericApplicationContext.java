package team.zavod.di.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.BeanPostProcessor;
import team.zavod.di.factory.ObjectProvider;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.BeanStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.SingletonBeanRegistry;
import team.zavod.di.factory.DefaultSingletonBeanRegistry;
import team.zavod.di.factory.exception.NoSuchBeanException;

public class GenericApplicationContext implements ApplicationContext {
  private String id;
  private final BeanFactory beanFactory;
  private final List<BeanPostProcessor> beanPostProcessors;
  private final SingletonBeanRegistry singletonBeanRegistry;

  public GenericApplicationContext(BeanFactory beanFactory) {
    this.id = generateId();
    this.beanFactory = beanFactory;
    this.beanPostProcessors = new ArrayList<>();
    this.singletonBeanRegistry = new DefaultSingletonBeanRegistry();
  }

  @Override
  public String getId() {
    return this.id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public <T> T getBean(Class<T> requiredType) throws BeanException {
    return this.beanFactory.getBean(requiredType);
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeanException {
    return this.beanFactory.getBean(name, requiredType);
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
    return this.beanFactory.getBeanProvider(requiredType);
  }

  @Override
  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return this.beanFactory.isSingleton(name);
  }

  @Override
  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return this.beanFactory.isPrototype(name);
  }

  @Override
  public boolean containsBean(String name) {
    return this.singletonBeanRegistry.containsSingleton(name);
  }

  @Override
  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    this.beanPostProcessors.add(beanPostProcessor);
  }

  @Override
  public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
    this.beanPostProcessors.addAll(beanPostProcessors);
  }

  @Override
  public List<BeanPostProcessor> getBeanPostProcessors() {
    return this.beanPostProcessors;
  }

  @Override
  public boolean containsBeanDefinition(String bean) {
    return this.beanFactory.containsBeanDefinition(bean);
  }

  @Override
  public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    this.beanFactory.registerBeanDefinition(beanDefinition);
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    this.beanFactory.removeBeanDefinition(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String bean) throws NoSuchBeanDefinitionException {
    return this.beanFactory.getBeanDefinition(bean);
  }

  @Override
  public Set<BeanDefinition> getBeanDefinitions(String beanClassName) throws NoSuchBeanDefinitionException {
    return this.beanFactory.getBeanDefinitions(beanClassName);
  }

  @Override
  public boolean containsSingleton(Class<?> beanType) {
    return this.singletonBeanRegistry.containsSingleton(beanType);
  }

  @Override
  public boolean containsSingleton(String beanName) {
    return this.singletonBeanRegistry.containsSingleton(beanName);
  }

  @Override
  public void registerSingleton(Class<?> beanType, String beanName, Object bean) throws BeanStoreException {
    this.singletonBeanRegistry.registerSingleton(beanType, beanName, bean);
  }

  @Override
  public void removeSingleton(String beanName) throws NoSuchBeanException {
    this.singletonBeanRegistry.removeSingleton(beanName);
  }

  @Override
  public Object getSingleton(Class<?> beanType) throws NoSuchBeanException {
    return this.singletonBeanRegistry.getSingleton(beanType);
  }

  @Override
  public Object getSingleton(String beanName) throws NoSuchBeanException {
    return this.singletonBeanRegistry.getSingleton(beanName);
  }

  @Override
  public Set<Object> getSingletons(Class<?> beanType) throws NoSuchBeanException {
    return this.singletonBeanRegistry.getSingletons(beanType);
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public boolean isRunning() {
    return false;  // TODO
  }

  private String generateId() {
    return getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(this));
  }
}
