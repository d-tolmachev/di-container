package team.zavod.di.configuration;

import team.zavod.di.configuration.metadata.ConfigurationMetadata;
import team.zavod.di.factory.ObjectProvider;

public interface BeanConfigurator {
  <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

  <T> Class<? extends T> getImplementationClass(Class<T> requiredType);

  ConfigurationMetadata getConfigurationMetadata();
}
