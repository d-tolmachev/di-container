package team.zavod.di.factory.exception;

import team.zavod.di.exception.FatalBeanException;

public class BeanDefinitionStoreException extends FatalBeanException {
  public BeanDefinitionStoreException() {
    super();
  }

  public BeanDefinitionStoreException(String message) {
    super(message);
  }

  public BeanDefinitionStoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanDefinitionStoreException(Throwable cause) {
    super(cause);
  }
}
