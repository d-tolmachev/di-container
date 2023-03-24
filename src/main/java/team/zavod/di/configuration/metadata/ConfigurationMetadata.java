package team.zavod.di.configuration.metadata;

import java.util.List;
import team.zavod.di.factory.BeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public interface ConfigurationMetadata {
  List<String> getPackagesToScan();

  BeanDefinitionRegistry getBeanDefinitionRegistry();

  ClasspathHelper getClasspathHelper();
}
