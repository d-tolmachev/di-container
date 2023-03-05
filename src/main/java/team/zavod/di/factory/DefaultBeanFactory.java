package team.zavod.di.factory;

import team.zavod.di.config.BeanDefinition;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.configuration.Configuration;
import team.zavod.di.exception.BeanException;

public class DefaultBeanFactory implements BeanFactory {
  private final BeanConfigurator beanConfigurator;
  private final BeanDefinitionRegistry beanDefinitionRegistry;

  public DefaultBeanFactory(Configuration configuration) {
    this.beanConfigurator = BeanConfiguratorFactory.newBeanConfigurator(configuration);
    this.beanDefinitionRegistry = this.beanConfigurator.getConfiguration().getBeanDefinitionRegistry();
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
  public boolean containsBeanDefinition(String beanName) {
    return this.beanDefinitionRegistry.containsBeanDefinition(beanName);
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
    this.beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
  }

  @Override
  public void removeBeanDefinition(String beanName) {
    this.beanDefinitionRegistry.removeBeanDefinition(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) {
    return this.beanDefinitionRegistry.getBeanDefinition(beanName);
  }
}
