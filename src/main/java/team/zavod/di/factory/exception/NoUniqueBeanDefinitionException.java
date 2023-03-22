package team.zavod.di.factory.exception;

public class NoUniqueBeanDefinitionException extends NoSuchBeanDefinitionException {
  public NoUniqueBeanDefinitionException() {
    super();
  }

  public NoUniqueBeanDefinitionException(String message) {
    super(message);
  }

  public NoUniqueBeanDefinitionException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoUniqueBeanDefinitionException(Throwable cause) {
    super(cause);
  }
}
