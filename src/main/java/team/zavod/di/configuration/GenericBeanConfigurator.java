package team.zavod.di.configuration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import team.zavod.di.configuration.metadata.ConfigurationMetadata;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.factory.exception.NoSuchBeanException;
import team.zavod.di.factory.exception.NoUniqueBeanException;
import team.zavod.di.util.ClasspathHelper;

public class GenericBeanConfigurator implements BeanConfigurator {
  private final ConfigurationMetadata configurationMetadata;
  private final ClasspathHelper classpathHelper;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private final Map<Class<?>, Class<?>> interfacesToImplementations;

  public GenericBeanConfigurator(ConfigurationMetadata configurationMetadata) {
    this.configurationMetadata = configurationMetadata;
    this.classpathHelper = this.configurationMetadata.getClasspathHelper();
    this.beanDefinitionRegistry = this.configurationMetadata.getBeanDefinitionRegistry();
    this.interfacesToImplementations = new ConcurrentHashMap<>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> Class<? extends T> getImplementationClass(Class<T> requiredType) throws NoSuchBeanException {
    if (!this.interfacesToImplementations.containsKey(requiredType)) {
      Set<Class<? extends T>> implementationClasses = this.classpathHelper.getSubTypesOf(requiredType).stream()
          .filter(subType -> this.beanDefinitionRegistry.containsBeanDefinition(subType.getName()))
          .collect(Collectors.toSet());
      if (implementationClasses.size() > 1) {
        throw new NoUniqueBeanException("Error! " + requiredType.getName() + " has more than one implementations!");
      } else if (!implementationClasses.isEmpty()) {
        this.interfacesToImplementations.put(requiredType, implementationClasses.iterator().next());
      } else throw new NoSuchBeanException("Error! " + requiredType.getName() + " has no implementations!");
    }
    return (Class<? extends T>) this.interfacesToImplementations.get(requiredType);
  }

  @Override
  public ConfigurationMetadata getConfigurationMetadata() {
    return this.configurationMetadata;
  }
}
