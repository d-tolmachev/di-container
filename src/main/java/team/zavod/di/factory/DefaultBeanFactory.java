package team.zavod.di.factory;

import java.util.Set;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.configuration.ConfigurationMetadata;
import team.zavod.di.configuration.GenericBeanConfigurator;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;

public class DefaultBeanFactory implements BeanFactory {
  private final BeanConfigurator beanConfigurator;
  private final BeanDefinitionRegistry beanDefinitionRegistry;

  public DefaultBeanFactory(ConfigurationMetadata configurationMetadata) {
    this.beanConfigurator = new GenericBeanConfigurator(configurationMetadata);
    this.beanDefinitionRegistry = this.beanConfigurator.getConfigurationMetadata().getBeanDefinitionRegistry();
  }

  @Override
  public <T> T getBean(Class<T> requiredType) throws BeanException {
    return null;  // TODO
  }

  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeanException {
    return null;  // TODO
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
    return this.beanConfigurator.getBeanProvider(requiredType);
  }

  @Override
  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinition(name).isSingleton();
  }

  @Override
  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinition(name).isPrototype();
  }

  @Override
  public boolean containsBeanDefinition(String bean) {
    return this.beanDefinitionRegistry.containsBeanDefinition(bean);
  }

  @Override
  public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    this.beanDefinitionRegistry.removeBeanDefinition(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String bean) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinition(bean);
  }

  @Override
  public Set<BeanDefinition> getBeanDefinitions(String beanClassName) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinitions(beanClassName);
  }
}
