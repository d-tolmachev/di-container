package team.zavod.di.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import team.zavod.di.config.dependency.ConstructorArgumentValues;
import team.zavod.di.config.dependency.MethodArgumentValues;
import team.zavod.di.config.dependency.PropertyValues;
import team.zavod.di.config.scope.PrototypeScope;
import team.zavod.di.config.scope.SingletonScope;
import team.zavod.di.config.scope.ThreadScope;

public class GenericBeanDefinition implements BeanDefinition {
  private String beanName;
  private String beanClassName;
  private String scope;
  private boolean lazyInit;
  private boolean primary;
  private String factoryBeanName;
  private String factoryMethodName;
  private String initMethodName;
  private String destroyMethodName;
  private ConstructorArgumentValues constructorArgumentValues;
  private PropertyValues propertyValues;
  private final Map<String, Set<MethodArgumentValues>> methodArgumentValues;

  public GenericBeanDefinition() {
    this.scope = SingletonScope.getName();
    this.lazyInit = false;
    this.primary = false;
    this.constructorArgumentValues = new ConstructorArgumentValues();
    this.propertyValues = new PropertyValues();
    this.methodArgumentValues = new HashMap<>();
  }

  @Override
  public String getBeanName() {
    return this.beanName;
  }

  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @Override
  public String getBeanClassName() {
    return this.beanClassName;
  }

  @Override
  public void setBeanClassName(String beanClassName) {
    this.beanClassName = beanClassName;
  }

  @Override
  public String getScope() {
    return this.scope;
  }

  @Override
  public void setScope(String scope) {
    this.scope = scope;
  }

  @Override
  public boolean isLazyInit() {
    return this.lazyInit;
  }

  @Override
  public void setLazyInit(boolean lazyInit) {
    this.lazyInit = lazyInit;
  }

  @Override
  public boolean isPrimary() {
    return this.primary;
  }

  @Override
  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  @Override
  public String getFactoryBeanName() {
    return this.factoryBeanName;
  }

  @Override
  public void setFactoryBeanName(String factoryBeanName) {
    this.factoryBeanName = factoryBeanName;
  }

  @Override
  public String getFactoryMethodName() {
    return this.factoryMethodName;
  }

  @Override
  public void setFactoryMethodName(String factoryMethodName) {
    this.factoryMethodName = factoryMethodName;
  }

  @Override
  public String getInitMethodName() {
    return this.initMethodName;
  }

  @Override
  public void setInitMethodName(String initMethodName) {
    this.initMethodName = initMethodName;
  }

  @Override
  public String getDestroyMethodName() {
    return this.destroyMethodName;
  }

  @Override
  public void setDestroyMethodName(String destroyMethodName) {
    this.destroyMethodName = destroyMethodName;
  }

  @Override
  public boolean isSingleton() {
    return this.scope.equals(SingletonScope.getName());
  }

  @Override
  public boolean isPrototype() {
    return this.scope.equals(PrototypeScope.getName());
  }

  @Override
  public boolean isThread() {
    return this.scope.equals(ThreadScope.getName());
  }

  @Override
  public boolean hasConstructorArgumentValues() {
    return Objects.nonNull(this.constructorArgumentValues) && !this.constructorArgumentValues.isEmpty();
  }

  @Override
  public ConstructorArgumentValues getConstructorArgumentValues() {
    return this.constructorArgumentValues;
  }

  @Override
  public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
    this.constructorArgumentValues = constructorArgumentValues;
  }

  @Override
  public boolean hasPropertyValues() {
    return Objects.nonNull(this.propertyValues) && !this.propertyValues.isEmpty();
  }

  @Override
  public PropertyValues getPropertyValues() {
    return this.propertyValues;
  }

  @Override
  public void setPropertyValues(PropertyValues propertyValues) {
    this.propertyValues = propertyValues;
  }

  @Override
  public boolean hasMethodArgumentValues(String methodName) {
    return this.methodArgumentValues.containsKey(methodName) && !this.methodArgumentValues.get(methodName).isEmpty() && !this.methodArgumentValues.get(methodName).iterator().next().isEmpty();
  }

  @Override
  public Set<MethodArgumentValues> getMethodArgumentValues(String methodName) {
    return this.methodArgumentValues.get(methodName);
  }

  @Override
  public void addMethodArgumentValues(String methodName, MethodArgumentValues methodArgumentValues) {
    this.methodArgumentValues.computeIfAbsent(methodName, k -> new HashSet<>()).add(methodArgumentValues);
  }

  @Override
  public List<String> getMethodNames() {
    return List.copyOf(this.methodArgumentValues.keySet());
  }
}
