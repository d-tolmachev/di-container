package team.zavod.di.factory;

import team.zavod.di.exception.BeanException;

@FunctionalInterface
public interface ObjectProvider<T> {
  T getObject() throws BeanException;
}
