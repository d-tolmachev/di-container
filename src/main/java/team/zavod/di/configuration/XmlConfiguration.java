package team.zavod.di.configuration;

import java.util.HashMap;
import java.util.Map;
import team.zavod.di.factory.BeanDefinitionRegistry;
import team.zavod.di.factory.SimpleBeanDefinitionRegistry;

public class XmlConfiguration implements Configuration {
  private static final ConfigurationTypes CONFIGURATION_TYPE = ConfigurationTypes.XML_CONFIGURATION;
  private String packageToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private final Map<Class<?>, Class<?>> interfacesToImplementations;

  public XmlConfiguration() {
    this.beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
    this.interfacesToImplementations = new HashMap<>();
  }

  @Override
  public String getPackageToScan() {
    return this.packageToScan;
  }

  @Override
  public BeanDefinitionRegistry getBeanDefinitionRegistry() {
    return this.beanDefinitionRegistry;
  }

  @Override
  public Map<Class<?>, Class<?>> getInterfacesToImplementations() {
    return this.interfacesToImplementations;
  }

  @Override
  public ConfigurationTypes getConfigurationType() {
    return CONFIGURATION_TYPE;
  }
}
