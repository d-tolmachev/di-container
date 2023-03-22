package team.zavod.di.factory.exception;

public class BeanDefinitionOverrideException extends BeanDefinitionStoreException {
  public BeanDefinitionOverrideException() {
    super();
  }

  public BeanDefinitionOverrideException(String message) {
    super(message);
  }

  public BeanDefinitionOverrideException(String message, Throwable cause) {
    super(message, cause);
  }

  public BeanDefinitionOverrideException(Throwable cause) {
    super(cause);
  }
}
