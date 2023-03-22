package team.zavod.di.configuration.exception;

import team.zavod.di.exception.ConfigurationException;

public class AnnotationConfigurationException extends ConfigurationException {
  public AnnotationConfigurationException() {
    super();
  }

  public AnnotationConfigurationException(String message) {
    super(message);
  }

  public AnnotationConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AnnotationConfigurationException(Throwable cause) {
    super(cause);
  }
}
