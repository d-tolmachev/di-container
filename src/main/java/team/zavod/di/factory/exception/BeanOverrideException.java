package team.zavod.di.factory.exception;

public class BeanOverrideException extends BeanStoreException {
  public BeanOverrideException() {
    super();
  }

  public BeanOverrideException(String message) {
    super(message);
  }

  public BeanOverrideException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanOverrideException(Throwable cause) {
    super(cause);
  }
}
