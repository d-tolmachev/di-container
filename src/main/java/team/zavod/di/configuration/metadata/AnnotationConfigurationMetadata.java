package team.zavod.di.configuration.metadata;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Lazy;
import team.zavod.di.annotation.Primary;
import team.zavod.di.annotation.Qualifier;
import team.zavod.di.annotation.Scope;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.ConstructorArgumentValues;
import team.zavod.di.config.GenericBeanDefinition;
import team.zavod.di.config.PropertyValues;
import team.zavod.di.configuration.exception.AnnotationConfigurationException;
import team.zavod.di.factory.BeanDefinitionRegistry;
import team.zavod.di.factory.SimpleBeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class AnnotationConfigurationMetadata implements ConfigurationMetadata {
  private static final Class<Component> COMPONENT_ANNOTATION = Component.class;
  private static final Class<Scope> SCOPE_ANNOTATION = Scope.class;
  private static final Class<Lazy> LAZY_ANNOTATION = Lazy.class;
  private static final Class<Primary> PRIMARY_ANNOTATION = Primary.class;
  private static final Class<Provider> PROVIDER_ANNOTATION = Provider.class;
  private static final Class<PostConstruct> POST_CONSTRUCT_ANNOTATION = PostConstruct.class;
  private static final Class<PreDestroy> PRE_DESTROY_ANNOTATION = PreDestroy.class;
  private static final List<Class<? extends Annotation>> AUTOWIRED_ANNOTATIONS = List.of(Autowired.class, Inject.class);
  private static final Class<Qualifier> QUALIFIER_ANNOTATION = Qualifier.class;
  private final List<String> packagesToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private ClasspathHelper classpathHelper;

  public AnnotationConfigurationMetadata(List<String> basePackages) {
    this.packagesToScan = new ArrayList<>(basePackages);
    this.beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
    parseAnnotationConfiguration();
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

  private void parseAnnotationConfiguration() {
    this.classpathHelper = new ClasspathHelper(this.packagesToScan);
    parseComponents(this.classpathHelper.getTypesAnnotatedWith(COMPONENT_ANNOTATION));
  }

  @SuppressWarnings("DuplicatedCode")
  private void parseComponents(Set<Class<?>> componentClasses) {
    Map<String, Set<BeanDefinition>> beanDefinitions = new HashMap<>();
    for (Class<?> componentClass : componentClasses) {
      Component component = componentClass.getAnnotation(COMPONENT_ANNOTATION);
      String beanName = !component.value().isEmpty() ? component.value() : generateBeanName(componentClass.getSimpleName());
      BeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanName(beanName);
      beanDefinition.setBeanClassName(componentClass.getName());
      if (componentClass.isAnnotationPresent(SCOPE_ANNOTATION)) {
        Scope scope = componentClass.getAnnotation(SCOPE_ANNOTATION);
        beanDefinition.setScope(scope.value());
      }
      if (componentClass.isAnnotationPresent(LAZY_ANNOTATION)) {
        Lazy lazy = componentClass.getAnnotation(LAZY_ANNOTATION);
        beanDefinition.setLazyInit(lazy.value());
      }
      if (componentClass.isAnnotationPresent(PRIMARY_ANNOTATION)) {
        Primary primary = componentClass.getAnnotation(PRIMARY_ANNOTATION);
        beanDefinition.setPrimary(primary.value());
      }
      parseProvider(componentClass, beanDefinition);
      parseInit(componentClass, beanDefinition);
      parseDestroy(componentClass, beanDefinition);
      parseInjects(componentClass, beanDefinition.getConstructorArgumentValues(), beanDefinition.getPropertyValues());
      if (!beanDefinitions.computeIfAbsent(beanDefinition.getBeanClassName(), k -> new HashSet<>()).add(beanDefinition)) {
        throw new AnnotationConfigurationException("Error! Failed to store multiple beans with the same name!");
      }
    }
    resolveBeansProviders(beanDefinitions);
    beanDefinitions.values().forEach(classBeanDefinitions -> classBeanDefinitions.forEach(this.beanDefinitionRegistry::registerBeanDefinition));
  }

  private void parseProvider(Class<?> componentClass, BeanDefinition beanDefinition) {
    List<Method> methods = Arrays.stream(componentClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PROVIDER_ANNOTATION)).toList();
    if (methods.size() > 1) {
      throw new AnnotationConfigurationException("Error! Failed to unambiguously determine factory method for " + componentClass.getName() + " class!");
    } else if (!methods.isEmpty()) {
      beanDefinition.setFactoryBeanName(methods.get(0).getReturnType().getName());
      beanDefinition.setFactoryMethodName(methods.get(0).getName());
    }
  }

  private void parseInit(Class<?> componentClass, BeanDefinition beanDefinition) {
    List<Method> methods = Arrays.stream(componentClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(POST_CONSTRUCT_ANNOTATION)).toList();
    if (methods.size() > 1) {
      throw new AnnotationConfigurationException("Error! Failed to unambiguously determine init method for " + componentClass.getName() + " class!");
    } else if (!methods.isEmpty()) {
      beanDefinition.setInitMethodName(methods.get(0).getName());
    }
  }

  private void parseDestroy(Class<?> componentClass, BeanDefinition beanDefinition) {
    List<Method> methods = Arrays.stream(componentClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PRE_DESTROY_ANNOTATION)).toList();
    if (methods.size() > 1) {
      throw new AnnotationConfigurationException("Error! Failed to unambiguously determine destroy method for " + componentClass.getName() + " class!");
    } else if (!methods.isEmpty()) {
      beanDefinition.setDestroyMethodName(methods.get(0).getName());
    }
  }

  private void parseInjects(Class<?> componentClass, ConstructorArgumentValues constructorArgumentValues, PropertyValues propertyValues) {
    Set<Constructor<?>> constructors = new HashSet<>();
    AUTOWIRED_ANNOTATIONS.forEach(AUTOWIRED_ANNOTATION -> constructors.addAll(Arrays.stream(componentClass.getDeclaredConstructors()).filter(constructor -> constructor.isAnnotationPresent(AUTOWIRED_ANNOTATION)).collect(Collectors.toSet())));
    if (constructors.size() > 1) {
      throw new AnnotationConfigurationException("Error! Failed to unambiguously determine constructor to autowire " + componentClass.getName() + "!");
    } else if (!constructors.isEmpty()) {
      parseConstructorArguments(constructors.iterator().next().getParameters(), constructorArgumentValues);
    }
    Set<Field> fields = new HashSet<>();
    AUTOWIRED_ANNOTATIONS.forEach(AUTOWIRED_ANNOTATION -> fields.addAll(Arrays.stream(componentClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(AUTOWIRED_ANNOTATION)).collect(Collectors.toSet())));
    parseProperties(fields, propertyValues);
  }

  private void parseConstructorArguments(Parameter[] parameters, ConstructorArgumentValues constructorArgumentValues) {
    for (int i = 0; i < parameters.length; i++) {
      Qualifier qualifier = parameters[i].getAnnotation(QUALIFIER_ANNOTATION);
      Class<?> type = Objects.nonNull(qualifier) ? qualifier.value() : parameters[i].getType();
      if (parameters[i].isNamePresent()) {
        constructorArgumentValues.addGenericArgumentValue(null, type.getName(), parameters[i].getName());
      } else constructorArgumentValues.addIndexedArgumentValue(i, null, type.getName());
    }
  }

  private void parseProperties(Set<Field> fields, PropertyValues propertyValues) {
    for (Field field : fields) {
      Qualifier qualifier = field.getAnnotation(QUALIFIER_ANNOTATION);
      Class<?> type = Objects.nonNull(qualifier) ? qualifier.value() : field.getType();
      propertyValues.addPropertyValue(null, type.getName(), field.getName());
    }
  }

  private void resolveBeansProviders(Map<String, Set<BeanDefinition>> beanDefinitions) {
    for (Set<BeanDefinition> classBeanDefinitions : beanDefinitions.values()) {
      for (BeanDefinition beanDefinition : classBeanDefinitions) {
        String factoryBeanClassName = beanDefinition.getFactoryBeanName();
        if (Objects.nonNull(factoryBeanClassName)) {
          Set<BeanDefinition> factoryBeanDefinitions = beanDefinitions.get(factoryBeanClassName);
          if (factoryBeanDefinitions.size() > 1) {
            throw new AnnotationConfigurationException("Error! Failed to unambiguously determine factory bean for " + beanDefinition.getBeanName() + " bean!");
          } else if (!factoryBeanDefinitions.isEmpty()) {
            beanDefinition.setFactoryBeanName(factoryBeanDefinitions.iterator().next().getBeanName());
          } else throw new AnnotationConfigurationException("Error! Failed to find factory bean for " + beanDefinition.getBeanName() + " bean!");
        }
      }
    }
  }

  private String generateBeanName(String simpleClassName) {
    return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
  }
}
