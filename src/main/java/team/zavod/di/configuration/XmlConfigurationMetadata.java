package team.zavod.di.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.ConstructorArgumentValues;
import team.zavod.di.config.StandardScope;
import team.zavod.di.config.GenericBeanDefinition;
import team.zavod.di.config.PropertyValues;
import team.zavod.di.config.ValueHolder;
import team.zavod.di.configuration.exception.XmlConfigurationException;
import team.zavod.di.factory.BeanDefinitionRegistry;
import team.zavod.di.factory.SimpleBeanDefinitionRegistry;
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
  private static final String BEAN_QUALIFIER_ATTRIBUTE = "qualifier";
  private final List<String> packagesToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private ClasspathHelper classpathHelper;

  public XmlConfigurationMetadata(String filename) {
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
      this.classpathHelper = new ClasspathHelper(this.packagesToScan);
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
    Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    for (int i = 0; i < beans.getLength(); i++) {
      Element bean = (Element) beans.item(i);
      BeanDefinition beanDefinition = new GenericBeanDefinition();
      if (!bean.hasAttribute(BEAN_NAME_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + BEAN_NAME_ATTRIBUTE + " attribute!");
      }
      beanDefinition.setBeanName(bean.getAttribute(BEAN_NAME_ATTRIBUTE));
      if (bean.hasAttribute(BEAN_QUALIFIER_ATTRIBUTE)) {
        beanDefinition.setBeanClassName(bean.getAttribute(BEAN_QUALIFIER_ATTRIBUTE));
      } else if (!bean.hasAttribute(BEAN_CLASS_NAME_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + BEAN_CLASS_NAME_ATTRIBUTE + " for " + bean.getAttribute(BEAN_NAME_ATTRIBUTE) + "!");
      } else beanDefinition.setBeanClassName(bean.getAttribute(BEAN_CLASS_NAME_ATTRIBUTE));
      if (bean.hasAttribute(SCOPE_ATTRIBUTE)) {
        StandardScope scope = switch (bean.getAttribute(SCOPE_ATTRIBUTE)) {
          case "singleton" -> StandardScope.SINGLETON;
          case "prototype" -> StandardScope.PROTOTYPE;
          case "thread" -> StandardScope.THREAD;
          default -> null;
        };
        if (Objects.isNull(scope)) {
          throw new XmlConfigurationException("Error! Invalid " + SCOPE_ATTRIBUTE + " value!");
        }
        beanDefinition.setScope(scope);
      }
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
      if (Objects.nonNull(beanDefinitions.putIfAbsent(beanDefinition.getBeanName(), beanDefinition))) {
        throw new XmlConfigurationException("Error! Failed to store multiple beans with the same name!");
      }
    }
    resolveBeansReferences(beanDefinitions);
    beanDefinitions.values().forEach(this.beanDefinitionRegistry::registerBeanDefinition);
  }

  private void parseConstructorArguments(NodeList constructorArguments, ConstructorArgumentValues constructorArgumentValues) {
    for (int i = 0; i < constructorArguments.getLength(); i++) {
      Element constructorArgument = (Element) constructorArguments.item(i);
      if (!constructorArgument.hasAttribute(REF_ATTRIBUTE)) {
        throw new XmlConfigurationException("Error! Failed to find " + REF_ATTRIBUTE + " attribute for " + CONSTRUCTOR_ARGUMENT_TAG + "!");
      }
      if (constructorArgument.hasAttribute(NAME_ATTRIBUTE)) {
        constructorArgumentValues.addGenericArgumentValue(null, constructorArgument.getAttribute(REF_ATTRIBUTE), constructorArgument.getAttribute(NAME_ATTRIBUTE));
      } else constructorArgumentValues.addIndexedArgumentValue(i, null, constructorArgument.getAttribute(REF_ATTRIBUTE));
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
      propertyValues.addPropertyValue(null, property.getAttribute(REF_ATTRIBUTE), property.getAttribute(NAME_ATTRIBUTE));
    }
  }

  private void resolveBeansReferences(Map<String, BeanDefinition> beanDefinitions) {
    for (BeanDefinition beanDefinition : beanDefinitions.values()) {
        resolveConstructorArgumentReferences(beanDefinitions, beanDefinition.getConstructorArgumentValues().getIndexedArgumentValues(), beanDefinition.getConstructorArgumentValues().getGenericArgumentValues());
        resolvePropertyReferences(beanDefinitions, beanDefinition.getPropertyValues().getPropertyValues());
    }
  }

  private void resolveConstructorArgumentReferences(Map<String, BeanDefinition> beanDefinitions, Map<Integer, ValueHolder> indexedArgumentValues, List<ValueHolder> genericArgumentValues) {
    for (ValueHolder valueHolder : indexedArgumentValues.values()) {
      if (!beanDefinitions.containsKey(valueHolder.getType())) {
        throw new XmlConfigurationException("Error! Failed to resolve reference for " + valueHolder.getType() + " bean!");
      }
      valueHolder.setType(beanDefinitions.get(valueHolder.getType()).getBeanClassName());
    }
    for (ValueHolder valueHolder : genericArgumentValues) {
      if (!beanDefinitions.containsKey(valueHolder.getType())) {
        throw new XmlConfigurationException("Error! Failed to resolve reference for " + valueHolder.getType() + " bean!");
      }
      valueHolder.setType(beanDefinitions.get(valueHolder.getType()).getBeanClassName());
    }
  }

  private void resolvePropertyReferences(Map<String, BeanDefinition> beanDefinitions, List<ValueHolder> propertyValues) {
    for (ValueHolder valueHolder : propertyValues) {
      if (!beanDefinitions.containsKey(valueHolder.getType())) {
        throw new XmlConfigurationException("Error! Failed to resolve reference for " + valueHolder.getType() + " bean!");
      }
      valueHolder.setType(beanDefinitions.get(valueHolder.getType()).getBeanClassName());
    }
  }
}
