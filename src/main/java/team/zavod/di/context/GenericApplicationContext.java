package team.zavod.di.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.BeanPostProcessor;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.BeanRegistry;
import team.zavod.di.factory.SimpleBeanRegistry;

public class GenericApplicationContext implements ApplicationContext {
  private String id;
  private final BeanFactory beanFactory;
  private final List<BeanPostProcessor> beanPostProcessors;
  private final BeanRegistry beanRegistry;

  public GenericApplicationContext(BeanFactory beanFactory) {
    this.id = generateId();
    this.beanFactory = beanFactory;
    this.beanPostProcessors = new ArrayList<>();
    this.beanRegistry = new SimpleBeanRegistry();
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
  public boolean containsBean(String name) {
    return this.beanRegistry.containsBean(name);
  }

  @Override
  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return this.beanFactory.getBeanDefinition(name).isSingleton();
  }

  @Override
  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return this.beanFactory.getBeanDefinition(name).isPrototype();
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
  public boolean containsBeanDefinition(String beanName) {
    return this.beanFactory.containsBeanDefinition(beanName);
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    this.beanFactory.removeBeanDefinition(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    return this.beanFactory.getBeanDefinition(beanName);
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
