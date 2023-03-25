package team.zavod.di.config.scope;

import team.zavod.di.factory.ObjectProvider;

public interface Scope {
  default boolean contains(String beanName) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  Object get(String beanName, Class<?> requiredType, ObjectProvider<?> objectProvider);

  default Object remove(String beanName) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}
