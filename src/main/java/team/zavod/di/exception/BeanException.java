package team.zavod.di.exception;

public abstract class BeanException extends RuntimeException {
  public BeanException() {
    super();
  }

  public BeanException(String message) {
    super(message);
  }

  public BeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanException(Throwable cause) {
    super(cause);
  }
}
