package team.zavod.di.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.factory.exception.BeanDefinitionOverrideException;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;

public class SimpleBeanDefinitionRegistry implements BeanDefinitionRegistry {
  private final Map<String, BeanDefinition> beanNamesToBeanDefinitions;

  public SimpleBeanDefinitionRegistry() {
    this.beanNamesToBeanDefinitions = new HashMap<>();
  }

  @Override
  public boolean containsBeanDefinition(String beanName) {
    return this.beanNamesToBeanDefinitions.containsKey(beanName);
  }

  @Override
  public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    if (Objects.isNull(this.beanNamesToBeanDefinitions.putIfAbsent(beanName, beanDefinition))) {
      throw new BeanDefinitionOverrideException();
    }
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    if (Objects.isNull(this.beanNamesToBeanDefinitions.remove(beanName))) {
      throw new NoSuchBeanDefinitionException();
    }
  }

  @Override
  public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    if (!this.beanNamesToBeanDefinitions.containsKey(beanName)) {
      throw new NoSuchBeanDefinitionException();
    }
    return this.beanNamesToBeanDefinitions.get(beanName);
  }
}
