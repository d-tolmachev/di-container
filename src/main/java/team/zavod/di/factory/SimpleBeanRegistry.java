package team.zavod.di.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import team.zavod.di.factory.exception.BeanOverrideException;
import team.zavod.di.factory.exception.BeanStoreException;
import team.zavod.di.factory.exception.NoSuchBeanException;
import team.zavod.di.factory.exception.NoUniqueBeanException;

public class SimpleBeanRegistry implements BeanRegistry {
  private final Map<Class<?>, Set<Object>> beanTypesToBeans;
  private final Map<String, Object> beanNamesToBeans;
  private final Map<String, Class<?>> beanNamesToBeanTypes;

  public SimpleBeanRegistry() {
    this.beanTypesToBeans = new ConcurrentHashMap<>();
    this.beanNamesToBeans = new ConcurrentHashMap<>();
    this.beanNamesToBeanTypes = new ConcurrentHashMap<>();
  }

  @Override
  public boolean containsBean(Class<?> beanType) {
    return this.beanTypesToBeans.containsKey(beanType);
  }

  @Override
  public boolean containsBean(String beanName) {
    return this.beanNamesToBeans.containsKey(beanName);
  }

  @Override
  public void registerBean(Class<?> beanType, String beanName, Object bean) throws BeanStoreException {
    if (Objects.isNull(this.beanNamesToBeans.putIfAbsent(beanName, bean))) {
      throw new BeanOverrideException();
    }
    this.beanTypesToBeans.computeIfAbsent(beanType, k -> new HashSet<>()).add(bean);
    this.beanNamesToBeanTypes.put(beanName, beanType);
  }

  @Override
  public void removeBean(String beanName) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.get(this.beanNamesToBeanTypes.get(beanName)).remove(this.beanNamesToBeans.get(beanName))) {
      throw new NoSuchBeanException();
    }
    this.beanNamesToBeans.remove(beanName);
    this.beanNamesToBeanTypes.remove(beanName);
  }

  @Override
  public Object getBean(Class<?> beanType) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.containsKey(beanType) || this.beanTypesToBeans.get(beanType).isEmpty()) {
      throw new NoSuchBeanException();
    }
    if (this.beanTypesToBeans.get(beanType).size() > 1) {
      throw new NoUniqueBeanException();
    }
    return this.beanTypesToBeans.get(beanType).iterator().next();
  }

  @Override
  public Object getBean(String beanName) throws NoSuchBeanException {
    if (!this.beanNamesToBeans.containsKey(beanName)) {
      throw new NoSuchBeanException();
    }
    return this.beanNamesToBeans.get(beanName);
  }

  @Override
  public Set<Object> getBeans(Class<?> beanType) throws NoSuchBeanException {
    if (!this.beanTypesToBeans.containsKey(beanType)) {
      throw new NoSuchBeanException();
    }
    return this.beanTypesToBeans.get(beanType);
  }
}
