package team.zavod.di.config;

public interface BeanDefinition {
  String getBeanClassName();

  void setBeanClassName(String beanClassName);

  DefaultScopes getScope();

  void setScope(DefaultScopes defaultScopes);

  boolean isLazyInit();

  void setLazyInit(boolean lazyInit);

  boolean isSingleton();

  boolean isPrototype();

  boolean hasConstructorArgumentValues();

  ConstructorArgumentValues getConstructorArgumentValues();

  void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues);

  boolean hasPropertyValues();

  PropertyValues getPropertyValues();

  void setPropertyValues(PropertyValues propertyValues);
}
