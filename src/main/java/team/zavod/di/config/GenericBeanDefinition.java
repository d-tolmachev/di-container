package team.zavod.di.config;

import java.util.Objects;

public class GenericBeanDefinition implements BeanDefinition {
  private String beanClassName;
  private DefaultScopes scope;
  private boolean lazyInit;
  private ConstructorArgumentValues constructorArgumentValues;
  private PropertyValues propertyValues;

  public GenericBeanDefinition() {
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
  public DefaultScopes getScope() {
    return this.scope;
  }

  @Override
  public void setScope(DefaultScopes scope) {
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
  public boolean isSingleton() {
    return this.scope == DefaultScopes.SINGLETON;
  }

  @Override
  public boolean isPrototype() {
    return this.scope == DefaultScopes.PROTOTYPE;
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
