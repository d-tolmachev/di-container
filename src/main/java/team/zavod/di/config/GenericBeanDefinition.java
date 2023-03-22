package team.zavod.di.config;

import java.util.Objects;

public class GenericBeanDefinition implements BeanDefinition {
  private String beanName;
  private String beanClassName;
  private StandardScope scope;
  private boolean lazyInit;
  private boolean primary;
  private String factoryBeanName;
  private String factoryMethodName;
  private String initMethodName;
  private String destroyMethodName;
  private ConstructorArgumentValues constructorArgumentValues;
  private PropertyValues propertyValues;

  public GenericBeanDefinition() {
    this.scope = StandardScope.SINGLETON;
    this.lazyInit = false;
    this.primary = false;
    this.constructorArgumentValues = new ConstructorArgumentValues();
    this.propertyValues = new PropertyValues();
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
  public StandardScope getScope() {
    return this.scope;
  }

  @Override
  public void setScope(StandardScope scope) {
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
    return this.scope == StandardScope.SINGLETON;
  }

  @Override
  public boolean isPrototype() {
    return this.scope == StandardScope.PROTOTYPE;
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
}
