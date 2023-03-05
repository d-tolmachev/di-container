package team.zavod.di.configuration;

import java.util.Map;
import team.zavod.di.util.ClasspathHelper;

public class XmlBeanConfigurator implements BeanConfigurator {
  private final Configuration configuration;
  private final ClasspathHelper classpathHelper;
  private final Map<Class<?>, Class<?>> interfacesToImplementations;

  public XmlBeanConfigurator(Configuration configuration) {
    this.configuration = configuration;
    this.classpathHelper = new ClasspathHelper(this.configuration.getPackageToScan());
    this.interfacesToImplementations = this.configuration.getInterfacesToImplementations();
  }

  @Override
  public <T> Class<? extends T> getImplementationClass(Class<T> type) {
    return null;  // TODO
  }

  @Override
  public Configuration getConfiguration() {
    return this.configuration;
  }
}
