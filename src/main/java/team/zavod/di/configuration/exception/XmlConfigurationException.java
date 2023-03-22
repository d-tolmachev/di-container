package team.zavod.di.configuration.exception;

import team.zavod.di.exception.ConfigurationException;

public class XmlConfigurationException extends ConfigurationException {
  public XmlConfigurationException() {
    super();
  }

  public XmlConfigurationException(String message) {
    super(message);
  }

  public XmlConfigurationException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlConfigurationException(Throwable cause) {
    super(cause);
  }
}
