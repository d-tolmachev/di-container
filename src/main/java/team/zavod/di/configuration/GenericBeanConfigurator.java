package team.zavod.di.configuration;

import java.util.HashMap;
import java.util.Map;
import team.zavod.di.factory.ObjectProvider;
import team.zavod.di.util.ClasspathHelper;

public class GenericBeanConfigurator implements BeanConfigurator {
  private final ConfigurationMetadata configurationMetadata;
  private final ClasspathHelper classpathHelper;
  private final Map<Class<?>, ObjectProvider<?>> typesToBeanProviders;
  private final Map<Class<?>, Class<?>> interfacesToImplementations;

  public GenericBeanConfigurator(ConfigurationMetadata configurationMetadata) {
    this.configurationMetadata = configurationMetadata;
    this.classpathHelper = this.configurationMetadata.getClasspathHelper();
    this.typesToBeanProviders = new HashMap<>();
    this.interfacesToImplementations = new HashMap<>();
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
    return null;  // TODO
  }

  @Override
  public <T> Class<? extends T> getImplementationClass(Class<T> requiredType) {
    return null;  // TODO
  }

  @Override
  public ConfigurationMetadata getConfigurationMetadata() {
    return this.configurationMetadata;
  }
}
