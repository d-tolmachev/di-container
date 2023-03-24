package team.zavod.di.configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import team.zavod.di.configuration.metadata.ConfigurationMetadata;
import team.zavod.di.exception.ConfigurationException;
import team.zavod.di.factory.ObjectProvider;
import team.zavod.di.util.ClasspathHelper;

public class GenericBeanConfigurator implements BeanConfigurator {
  private final ConfigurationMetadata configurationMetadata;
  private final ClasspathHelper classpathHelper;
  private final Map<Class<?>, ObjectProvider<?>> typesToBeanProviders;
  private final Map<Class, Class> interfacesToImplementations;

  public GenericBeanConfigurator(ConfigurationMetadata configurationMetadata) {
    this.configurationMetadata = configurationMetadata;
    this.classpathHelper = this.configurationMetadata.getClasspathHelper();
    this.typesToBeanProviders = new HashMap<>();
    this.interfacesToImplementations = new ConcurrentHashMap<>();
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
    return null;  // TODO
  }

  @Override
  public <T> Class<? extends T> getImplementationClass(Class<T> requiredType) {
    return interfacesToImplementations.computeIfAbsent(requiredType, clazz -> {
      Set<Class<? extends T>> implementationClasses = classpathHelper.getSubTypesOf(requiredType);
      if (implementationClasses.size() != 1) {
        throw new RuntimeException("Interface has 0 or more than 1 implementations");
      }

      return implementationClasses.stream().findFirst().get();
    });
  }

  @Override
  public ConfigurationMetadata getConfigurationMetadata() {
    return this.configurationMetadata;
  }
}
