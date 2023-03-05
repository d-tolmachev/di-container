package team.zavod.di.configuration;

import java.util.Map;
import team.zavod.di.factory.BeanDefinitionRegistry;

public interface Configuration {
  String getPackageToScan();

  BeanDefinitionRegistry getBeanDefinitionRegistry();

  Map<Class<?>, Class<?>> getInterfacesToImplementations();

  ConfigurationTypes getConfigurationType();
}
