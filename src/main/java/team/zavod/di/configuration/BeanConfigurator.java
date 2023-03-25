package team.zavod.di.configuration;

import team.zavod.di.configuration.metadata.ConfigurationMetadata;
import team.zavod.di.factory.exception.NoSuchBeanException;

public interface BeanConfigurator {
  <T> Class<? extends T> getImplementationClass(Class<T> requiredType) throws NoSuchBeanException;

  ConfigurationMetadata getConfigurationMetadata();
}
