package team.zavod.di.config;

import java.util.List;
import java.util.Set;
import team.zavod.di.config.dependency.ConstructorArgumentValues;
import team.zavod.di.config.dependency.MethodArgumentValues;
import team.zavod.di.config.dependency.PropertyValues;

public interface BeanDefinition {
  String getBeanName();

  void setBeanName(String beanName);

  String getBeanClassName();

  void setBeanClassName(String beanClassName);

  String getScope();

  void setScope(String scope);

  boolean isLazyInit();

  void setLazyInit(boolean lazyInit);

  boolean isPrimary();

  void setPrimary(boolean primary);

  String getFactoryBeanName();

  void setFactoryBeanName(String factoryBeanName);

  String getFactoryMethodName();

  void setFactoryMethodName(String factoryMethodName);

  String getInitMethodName();

  void setInitMethodName(String initMethodName);

  String getDestroyMethodName();

  void setDestroyMethodName(String destroyMethodName);

  boolean isSingleton();

  boolean isPrototype();

  boolean isThread();

  boolean hasConstructorArgumentValues();

  ConstructorArgumentValues getConstructorArgumentValues();

  void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues);

  boolean hasPropertyValues();

  PropertyValues getPropertyValues();

  void setPropertyValues(PropertyValues propertyValues);

  boolean hasMethodArgumentValues(String methodName);

  Set<MethodArgumentValues> getMethodArgumentValues(String methodName);

  void addMethodArgumentValues(String methodName, MethodArgumentValues methodArgumentValues);

  List<String> getMethodNames();
}
