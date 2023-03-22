package team.zavod.di.configuration.exception;

import team.zavod.di.exception.ConfigurationException;

public class JavaConfigurationException extends ConfigurationException {
  public JavaConfigurationException() {
    super();
  }

  public JavaConfigurationException(String message) {
    super(message);
  }

  public JavaConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public JavaConfigurationException(Throwable cause) {
    super(cause);
  }
}
