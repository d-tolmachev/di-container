package team.zavod.di.configuration.metadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.dependency.ConstructorArgumentValues;
import team.zavod.di.config.GenericBeanDefinition;
import team.zavod.di.config.dependency.PropertyValues;
import team.zavod.di.config.dependency.ValueHolder;
import team.zavod.di.configuration.exception.XmlConfigurationException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.factory.registry.SimpleBeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class XmlConfigurationMetadata implements ConfigurationMetadata {
  private static final String BASE_PACKAGE_TAG = "base-package";
  private static final String BEAN_TAG = "bean";
  private static final String BEAN_NAME_ATTRIBUTE = "id";
  private static final String BEAN_CLASS_NAME_ATTRIBUTE = "class";
  private static final String SCOPE_ATTRIBUTE = "scope";
  private static final String LAZY_INIT_ATTRIBUTE = "lazy-init";
  private static final String PRIMARY_ATTRIBUTE = "primary";
  private static final String FACTORY_BEAN_ATTRIBUTE = "factory-bean";
  private static final String FACTORY_METHOD_ATTRIBUTE = "factory-method";
  private static final String INIT_METHOD_ATTRIBUTE = "init-method";
  private static final String DESTROY_METHOD_ATTRIBUTE = "destroy-method";
  private static final String CONSTRUCTOR_ARGUMENT_TAG = "constructor-arg";
  private static final String PROPERTY_TAG = "property";
  private static final String NAME_ATTRIBUTE = "name";
  private static final String REF_ATTRIBUTE = "ref";
  private final ClassLoader classLoader;
  private final List<String> packagesToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private ClasspathHelper classpathHelper;

  public XmlConfigurationMetadata(String filename) {
    this(filename, null);
  }

  public XmlConfigurationMetadata(String filename, ClassLoader classLoader) {
    this.classLoader = classLoader;
    this.packagesToScan = new ArrayList<>();
    this.beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
    parseXmlConfiguration(filename);
  }

  @Override
  public List<String> getPackagesToScan() {
    return this.packagesToScan;
  }

  @Override
  public BeanDefinitionRegistry getBeanDefinitionRegistry() {
    return this.beanDefinitionRegistry;
  }

  @Override
  public ClasspathHelper getClasspathHelper() {
    return this.classpathHelper;
  }

  private void parseXmlConfiguration(String filename) {
    try {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(filename);
      parseBasePackages(document.getElementsByTagName(BASE_PACKAGE_TAG));
      this.classpathHelper = Objects.nonNull(this.classLoader) ? new ClasspathHelper(this.packagesToScan, this.classLoader) : new ClasspathHelper(this.packagesToScan);
      parseBeans(document.getElementsByTagName(BEAN_TAG));
    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new XmlConfigurationException("Error! Failed to read XML configuration!", e);
    }
  }

  private void parseBasePackages(NodeList basePackages) {
    if (basePackages.getLength() == 0) {
      throw new XmlConfigurationException("Error! Failed to find base packages specification!");
    }
    for (int i = 0; i < basePackages.getLength(); i++) {
      Element basePackage = (Element) basePackages.item(i);
      if (basePackage.getTextContent().isEmpty()) {
        throw new XmlConfigurationException("Error! Invalid " + BASE_PACKAGE_TAG + " value!");
      }
      this.packagesToScan.add(basePackage.getTextContent());
    }
  }

  @SuppressWarnings("DuplicatedCode")
  private void parseBeans(NodeList beans) {
    if (beans.getLength() == 0) {
      throw new XmlConfigurationException("Error! Failed to find beans specification!");
    }
    for (int i = 0; i < beans.getLength(); i++) {
      Element bean = (Element) beans.item(i);
      BeanDefinition beanDefinition = new GenericBeanDefinition();
      if (!bean.hasAttribute(BEAN_NAME_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + BEAN_NAME_ATTRIBUTE + " attribute!");
      }
      beanDefinition.setBeanName(bean.getAttribute(BEAN_NAME_ATTRIBUTE));
      if (!bean.hasAttribute(BEAN_CLASS_NAME_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + BEAN_CLASS_NAME_ATTRIBUTE + " for " + bean.getAttribute(BEAN_NAME_ATTRIBUTE) + "!");
      }
      beanDefinition.setBeanClassName(bean.getAttribute(BEAN_CLASS_NAME_ATTRIBUTE));
      if (bean.hasAttribute(SCOPE_ATTRIBUTE)) {
        beanDefinition.setScope(bean.getAttribute(SCOPE_ATTRIBUTE));
      }
      parseLazyInit(bean, beanDefinition);
      parsePrimary(bean, beanDefinition);
      if (bean.hasAttribute(FACTORY_BEAN_ATTRIBUTE)) {
        beanDefinition.setFactoryBeanName(bean.getAttribute(FACTORY_BEAN_ATTRIBUTE));
      }
      if (bean.hasAttribute(FACTORY_METHOD_ATTRIBUTE)) {
        beanDefinition.setFactoryMethodName(bean.getAttribute(FACTORY_METHOD_ATTRIBUTE));
      }
      if (bean.hasAttribute(INIT_METHOD_ATTRIBUTE)) {
        beanDefinition.setInitMethodName(bean.getAttribute(INIT_METHOD_ATTRIBUTE));
      }
      if (bean.hasAttribute(DESTROY_METHOD_ATTRIBUTE)) {
        beanDefinition.setDestroyMethodName(bean.getAttribute(DESTROY_METHOD_ATTRIBUTE));
      }
      parseConstructorArguments(bean.getElementsByTagName(CONSTRUCTOR_ARGUMENT_TAG), beanDefinition.getConstructorArgumentValues());
      parseProperties(bean.getElementsByTagName(PROPERTY_TAG), beanDefinition.getPropertyValues());
      this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
    }
    resolveBeansInjects();
  }

  @SuppressWarnings("DuplicatedCode")
  private void parseLazyInit(Element bean, BeanDefinition beanDefinition) {
    if (bean.hasAttribute(LAZY_INIT_ATTRIBUTE)) {
      Boolean lazyInit = switch(bean.getAttribute(LAZY_INIT_ATTRIBUTE)) {
        case "true" -> Boolean.TRUE;
        case "false" -> Boolean.FALSE;
        default -> null;
      };
      if (Objects.isNull(lazyInit)) {
        throw new XmlConfigurationException("Error! Invalid " + LAZY_INIT_ATTRIBUTE + " value!");
      }
      beanDefinition.setLazyInit(lazyInit);
    }
  }

  @SuppressWarnings("DuplicatedCode")
  private void parsePrimary(Element bean, BeanDefinition beanDefinition) {
    if (bean.hasAttribute(PRIMARY_ATTRIBUTE)) {
      Boolean primary = switch(bean.getAttribute(PRIMARY_ATTRIBUTE)) {
        case "true" -> Boolean.TRUE;
        case "false" -> Boolean.FALSE;
        default -> null;
      };
      if (Objects.isNull(primary)) {
        throw new XmlConfigurationException("Error! Invalid " + PRIMARY_ATTRIBUTE + " value!");
      }
      beanDefinition.setPrimary(primary);
    }
  }

  private void parseConstructorArguments(NodeList constructorArguments, ConstructorArgumentValues constructorArgumentValues) {
    for (int i = 0; i < constructorArguments.getLength(); i++) {
      Element constructorArgument = (Element) constructorArguments.item(i);
      if (!constructorArgument.hasAttribute(REF_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + REF_ATTRIBUTE + " attribute for " + CONSTRUCTOR_ARGUMENT_TAG + "!");
      }
      if (constructorArgument.hasAttribute(NAME_ATTRIBUTE)) {
        constructorArgumentValues.addGenericArgumentValue(null, null, constructorArgument.getAttribute(NAME_ATTRIBUTE), constructorArgument.getAttribute(REF_ATTRIBUTE));
      } else constructorArgumentValues.addIndexedArgumentValue(i, null, null, constructorArgument.getAttribute(REF_ATTRIBUTE));
    }
  }

  private void parseProperties(NodeList properties, PropertyValues propertyValues) {
    for (int i = 0; i < properties.getLength(); i++) {
      Element property = (Element) properties.item(i);
      if (!property.hasAttribute(REF_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + REF_ATTRIBUTE + " attribute for " + PROPERTY_TAG + "!");
      }
      if (!property.hasAttribute(NAME_ATTRIBUTE)) {
        throw new XmlConfigurationException("Failed to find " + NAME_ATTRIBUTE + " attribute for " + PROPERTY_TAG + "!");
      }
      propertyValues.addPropertyValue(null, null, property.getAttribute(NAME_ATTRIBUTE), property.getAttribute(REF_ATTRIBUTE));
    }
  }

  @SuppressWarnings("DuplicatedCode")
  private void resolveBeansInjects() {
    for (String beanName : this.beanDefinitionRegistry.getBeanDefinitionNames()) {
      BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
      beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues().values().forEach(this::resolveValueHolder);
      beanDefinition.getConstructorArgumentValues().getGenericArgumentValues().forEach(this::resolveValueHolder);
      beanDefinition.getPropertyValues().getPropertyValues().forEach(this::resolveValueHolder);
    }
  }

  private void resolveValueHolder(ValueHolder valueHolder) {
    valueHolder.setType(this.beanDefinitionRegistry.getBeanDefinition(valueHolder.getBeanName()).getBeanClassName());
  }
}
