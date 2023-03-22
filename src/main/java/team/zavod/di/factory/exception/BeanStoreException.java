package team.zavod.di.factory.exception;

import team.zavod.di.exception.FatalBeanException;

public class BeanStoreException extends FatalBeanException {
  public BeanStoreException() {
    super();
  }

  public BeanStoreException(String message) {
    super(message);
  }

  public BeanStoreException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanStoreException(Throwable cause) {
    super(cause);
  }
}
