package team.zavod.di.factory.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.factory.exception.BeanDefinitionOverrideException;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.exception.NoUniqueBeanDefinitionException;

public class SimpleBeanDefinitionRegistry implements BeanDefinitionRegistry {
  private final Map<String, BeanDefinition> beanNamesToBeanDefinitions;
  private final Map<String, Set<BeanDefinition>> beanClassNamesToBeanDefinitions;
  private final Map<String, String> beanNamesToBeanClassNames;

  public SimpleBeanDefinitionRegistry() {
    this.beanNamesToBeanDefinitions = new HashMap<>();
    this.beanClassNamesToBeanDefinitions = new HashMap<>();
    this.beanNamesToBeanClassNames = new HashMap<>();
  }

  @Override
  public boolean containsBeanDefinition(String bean) {
    return this.beanNamesToBeanDefinitions.containsKey(bean) || (this.beanClassNamesToBeanDefinitions.containsKey(bean) && !this.beanClassNamesToBeanDefinitions.get(bean).isEmpty());
  }

  @Override
  public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    if (Objects.nonNull(this.beanNamesToBeanDefinitions.putIfAbsent(beanDefinition.getBeanName(), beanDefinition))) {
      throw new BeanDefinitionOverrideException("Error! Failed to register bean definition for " + beanDefinition.getBeanName() + "!");
    }
    this.beanClassNamesToBeanDefinitions.computeIfAbsent(beanDefinition.getBeanClassName(), k -> new HashSet<>()).add(beanDefinition);
    this.beanNamesToBeanClassNames.put(beanDefinition.getBeanName(), beanDefinition.getBeanClassName());
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    if (!this.beanClassNamesToBeanDefinitions.get(this.beanNamesToBeanClassNames.get(beanName)).remove(this.beanNamesToBeanDefinitions.get(beanName))) {
      throw new NoSuchBeanDefinitionException("Error! Failed to find bean definition for " + beanName + "!");
    }
    this.beanNamesToBeanDefinitions.remove(beanName);
    this.beanNamesToBeanClassNames.remove(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String bean) throws NoSuchBeanDefinitionException {
    if (this.beanNamesToBeanDefinitions.containsKey(bean)) {
      return this.beanNamesToBeanDefinitions.get(bean);
    }
    if (!this.beanClassNamesToBeanDefinitions.containsKey(bean) || this.beanClassNamesToBeanDefinitions.get(bean).isEmpty()) {
      throw new NoSuchBeanDefinitionException("Error! Failed to determine bean definition for " + bean + "!");
    }
    if (this.beanClassNamesToBeanDefinitions.get(bean).size() > 1) {
      throw new NoUniqueBeanDefinitionException("Error! Failed to unambiguously determine bean definition for " + bean + "!");
    }
    return this.beanClassNamesToBeanDefinitions.get(bean).iterator().next();
  }

  @Override
  public Set<BeanDefinition> getBeanDefinitions(String beanClassName) throws NoSuchBeanDefinitionException {
    if (!this.beanClassNamesToBeanDefinitions.containsKey(beanClassName)) {
      throw new NoSuchBeanDefinitionException("Error! Failed to determine bean definition for " + beanClassName + "!");
    }
    return this.beanClassNamesToBeanDefinitions.get(beanClassName);
  }

  public List<String> getBeanDefinitionNames() {
    return List.copyOf(this.beanNamesToBeanDefinitions.keySet());
  }
}
