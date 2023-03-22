package team.zavod.di.factory.exception;

import team.zavod.di.exception.BeanException;

public class BeanNotOfRequiredTypeException extends BeanException {
  public BeanNotOfRequiredTypeException() {
    super();
  }

  public BeanNotOfRequiredTypeException(String message) {
    super(message);
  }

  public BeanNotOfRequiredTypeException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanNotOfRequiredTypeException(Throwable cause) {
    super(cause);
  }
}
