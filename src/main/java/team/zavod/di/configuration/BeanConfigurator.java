package team.zavod.di.configuration;

public interface BeanConfigurator {
  <T> Class<? extends T> getImplementationClass(Class<T> type);

  Configuration getConfiguration();
}
