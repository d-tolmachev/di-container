package team.zavod.di.factory.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import team.zavod.di.factory.exception.BeanOverrideException;
import team.zavod.di.factory.exception.BeanStoreException;
import team.zavod.di.factory.exception.NoSuchBeanException;
import team.zavod.di.factory.exception.NoUniqueBeanException;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
  private final Map<Class<?>, Set<Object>> beanTypesToBeans;
  private final Map<String, Object> beanNamesToBeans;
  private final Map<String, Class<?>> beanNamesToBeanTypes;

  public DefaultSingletonBeanRegistry() {
    this.beanTypesToBeans = new ConcurrentHashMap<>();
    this.beanNamesToBeans = new ConcurrentHashMap<>();
    this.beanNamesToBeanTypes = new ConcurrentHashMap<>();
  }

  @Override
  public boolean containsSingleton(Class<?> beanType) {
    return this.beanTypesToBeans.containsKey(beanType) && !this.beanTypesToBeans.get(beanType).isEmpty();
  }

  @Override
  public boolean containsSingleton(String beanName) {
    return this.beanNamesToBeans.containsKey(beanName);
  }

  @Override
  public void registerSingleton(Class<?> beanType, String beanName, Object bean) throws BeanStoreException {
    if (Objects.nonNull(this.beanNamesToBeans.putIfAbsent(beanName, bean))) {
      throw new BeanOverrideException("Error! Failed to register " + beanName + " bean !");
    }
    this.beanTypesToBeans.computeIfAbsent(beanType, k -> new HashSet<>()).add(bean);
    this.beanNamesToBeanTypes.put(beanName, beanType);
  }

  @Override
  public void removeSingleton(String beanName) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.get(this.beanNamesToBeanTypes.get(beanName)).remove(this.beanNamesToBeans.get(beanName))) {
      throw new NoSuchBeanException("Error! Failed to find " + beanName + " bean!");
    }
    this.beanNamesToBeans.remove(beanName);
    this.beanNamesToBeanTypes.remove(beanName);
  }

  @Override
  public Object getSingleton(Class<?> beanType) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.containsKey(beanType) || this.beanTypesToBeans.get(beanType).isEmpty()) {
      throw new NoSuchBeanException("Error! Failed to determine bean for " + beanType.getName() + "!");
    }
    if (this.beanTypesToBeans.get(beanType).size() > 1) {
      throw new NoUniqueBeanException("Error! Failed to unambiguously determine bean for " + beanType.getName() + "!");
    }
    return this.beanTypesToBeans.get(beanType).iterator().next();
  }

  @Override
  public Object getSingleton(String beanName) throws NoSuchBeanException {
    if (!this.beanNamesToBeans.containsKey(beanName)) {
      throw new NoSuchBeanException("Error! Failed to find " + beanName + " bean!");
    }
    return this.beanNamesToBeans.get(beanName);
  }

  @Override
  public Set<Object> getSingletons(Class<?> beanType) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.containsKey(beanType)) {
      throw new NoSuchBeanException("Error! Failed to determine bean for " + beanType.getName() + "!");
    }
    return this.beanTypesToBeans.get(beanType);
  }

  @Override
  public List<String> getSingletonNames() {
    return List.copyOf(this.beanNamesToBeans.keySet());
  }
}
