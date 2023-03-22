package team.zavod.di.factory.exception;

import team.zavod.di.exception.BeanException;

public class NoSuchBeanDefinitionException extends BeanException {
  public NoSuchBeanDefinitionException() {
    super();
  }

  public NoSuchBeanDefinitionException(String message) {
    super(message);
  }

  public NoSuchBeanDefinitionException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoSuchBeanDefinitionException(Throwable cause) {
    super(cause);
  }
}
