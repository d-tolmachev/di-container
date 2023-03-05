package team.zavod.di.factory;

import team.zavod.di.configuration.AnnotationsBeanConfigurator;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.configuration.Configuration;
import team.zavod.di.configuration.JavaBeanConfigurator;
import team.zavod.di.configuration.XmlBeanConfigurator;

public class BeanConfiguratorFactory {
  static BeanConfigurator newBeanConfigurator(Configuration configuration) {
    return switch (configuration.getConfigurationType()) {
      case ANNOTATIONS_CONFIGURATION -> new AnnotationsBeanConfigurator(configuration);
      case JAVA_CONFIGURATION -> new JavaBeanConfigurator(configuration);
      case XML_CONFIGURATION -> new XmlBeanConfigurator(configuration);
    };
  }
}
