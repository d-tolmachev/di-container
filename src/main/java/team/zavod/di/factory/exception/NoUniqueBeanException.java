package team.zavod.di.factory.exception;

public class NoUniqueBeanException extends NoSuchBeanException {
  public NoUniqueBeanException() {
    super();
  }

  public NoUniqueBeanException(String message) {
    super(message);
  }

  public NoUniqueBeanException(String message, Throwable cause) {
    super(message, cause);
  }

  public NoUniqueBeanException(Throwable cause) {
    super(cause);
  }
}
