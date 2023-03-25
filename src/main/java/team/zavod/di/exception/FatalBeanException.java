package team.zavod.di.exception;

public class FatalBeanException extends BeanException {
  public FatalBeanException() {
    super();
  }

  public FatalBeanException(String message) {
    super(message);
  }

  public FatalBeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public FatalBeanException(Throwable cause) {
    super(cause);
  }
}
