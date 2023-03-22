package team.zavod.di.factory.exception;

import team.zavod.di.exception.BeanException;

public class NoSuchBeanException extends BeanException {
  public NoSuchBeanException() {
    super();
  }

  public NoSuchBeanException(String message) {
    super(message);
  }

  public NoSuchBeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoSuchBeanException(Throwable cause) {
    super(cause);
  }
}
