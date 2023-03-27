package team.zavod.di.configuration.metadata;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Lazy;
import team.zavod.di.annotation.Primary;
import team.zavod.di.annotation.Scope;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.dependency.ArgumentValues;
import team.zavod.di.config.GenericBeanDefinition;
import team.zavod.di.config.dependency.MethodArgumentValues;
import team.zavod.di.config.dependency.PropertyValues;
import team.zavod.di.configuration.exception.AnnotationConfigurationException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.factory.registry.SimpleBeanDefinitionRegistry;
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
  private static final Class<Named> NAMED_ANNOTATION = Named.class;
  private final ClassLoader classLoader;
  private final List<String> packagesToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private ClasspathHelper classpathHelper;

  public AnnotationConfigurationMetadata(String[] basePackages) {
    this(basePackages, null);
  }

  public AnnotationConfigurationMetadata(String[] basePackages, ClassLoader classLoader) {
    this.classLoader = classLoader;
    this.packagesToScan = Arrays.asList(basePackages);
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
    this.classpathHelper = Objects.nonNull(this.classLoader) ? new ClasspathHelper(this.packagesToScan, this.classLoader) : new ClasspathHelper(this.packagesToScan);
    parseComponents(this.classpathHelper.getTypesAnnotatedWith(COMPONENT_ANNOTATION));
  }

  @SuppressWarnings("DuplicatedCode")
  private void parseComponents(Set<Class<?>> componentClasses) {
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
      parseInjects(componentClass, beanDefinition);
      this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
    }
    resolveBeansProviders();
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

  private void parseInjects(Class<?> componentClass, BeanDefinition beanDefinition) {
    Set<Constructor<?>> constructors = new HashSet<>();
    AUTOWIRED_ANNOTATIONS.forEach(AUTOWIRED_ANNOTATION -> constructors.addAll(Arrays.stream(componentClass.getDeclaredConstructors()).filter(constructor -> constructor.isAnnotationPresent(AUTOWIRED_ANNOTATION)).collect(Collectors.toSet())));
    if (constructors.size() > 1) {
      throw new AnnotationConfigurationException("Error! Failed to unambiguously determine constructor to autowire " + componentClass.getName() + "!");
    } else if (!constructors.isEmpty()) {
      parseArguments(constructors.iterator().next().getParameters(), beanDefinition.getConstructorArgumentValues());
    }
    Set<Field> fields = new HashSet<>();
    AUTOWIRED_ANNOTATIONS.forEach(AUTOWIRED_ANNOTATION -> fields.addAll(Arrays.stream(componentClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(AUTOWIRED_ANNOTATION)).collect(Collectors.toSet())));
    parseProperties(fields, beanDefinition.getPropertyValues());
    Set<Method> methods = new HashSet<>();
    AUTOWIRED_ANNOTATIONS.forEach(AUTOWIRED_ANNOTATION -> methods.addAll(Arrays.stream(componentClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(AUTOWIRED_ANNOTATION)).collect(Collectors.toSet())));
    parseMethods(methods, beanDefinition);
  }

  private void parseProperties(Set<Field> fields, PropertyValues propertyValues) {
    for (Field field : fields) {
      Named named = field.getAnnotation(NAMED_ANNOTATION);
      String type = field.getType().getName();
      String beanName = Objects.nonNull(named) ? named.value() : null;
      propertyValues.addGenericValue(null, type, field.getName(), beanName);
    }
  }

  private void parseMethods(Set<Method> methods, BeanDefinition beanDefinition) {
    for (Method method : methods) {
      MethodArgumentValues methodArgumentValues = new MethodArgumentValues(method.getName());
      parseArguments(method.getParameters(), methodArgumentValues);
      beanDefinition.addMethodArgumentValues(method.getName(), methodArgumentValues);
    }
  }

  private void parseArguments(Parameter[] parameters, ArgumentValues argumentValues) {
    for (int i = 0; i < parameters.length; i++) {
      Named named = parameters[i].getAnnotation(NAMED_ANNOTATION);
      String type = parameters[i].getType().getName();
      String beanName = Objects.nonNull(named) ? named.value() : null;
      if (parameters[i].isNamePresent()) {
        argumentValues.addGenericValue(null, type, parameters[i].getName(), beanName);
      } else argumentValues.addIndexedValue(i, null, type, beanName);
    }
  }

  @SuppressWarnings("unchecked")
  private void resolveBeansProviders() {
    for (String beanName : this.beanDefinitionRegistry.getBeanNames()) {
      BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
      if (Objects.nonNull(beanDefinition.getFactoryBeanName())) {
        Class<?> factoryBeanClass = this.classpathHelper.classForName(beanDefinition.getFactoryBeanName());
        if (factoryBeanClass.isInterface()) {
          Set<Class<?>> subTypes = (Set<Class<?>>) this.classpathHelper.getSubTypesOf(factoryBeanClass);
          if (subTypes.size() > 1) {
            throw new AnnotationConfigurationException("Error! Failed to unambiguously determine factory bean for " + beanDefinition.getBeanName() + " bean!");
          } else if (!subTypes.isEmpty()) {
            factoryBeanClass = subTypes.iterator().next();
          } else throw new AnnotationConfigurationException("Error! Failed to find factory bean for " + beanDefinition.getBeanName() + " bean!");
        }
        Set<BeanDefinition> factoryBeanDefinitions = this.beanDefinitionRegistry.getBeanDefinitions(factoryBeanClass.getName());
        if (factoryBeanDefinitions.size() > 1) {
          throw new AnnotationConfigurationException("Error! Failed to unambiguously determine factory bean for " + beanDefinition.getBeanName() + " bean!");
        } else if (!factoryBeanDefinitions.isEmpty()) {
          BeanDefinition factoryBeanDefinition = factoryBeanDefinitions.iterator().next();
          factoryBeanDefinition.setFactoryBeanName(beanDefinition.getBeanClassName());
          beanDefinition.setFactoryBeanName(null);
          factoryBeanDefinition.setFactoryMethodName(beanDefinition.getFactoryMethodName());
          beanDefinition.setFactoryMethodName(null);
        } else throw new AnnotationConfigurationException("Error! Failed to find factory bean for " + beanDefinition.getBeanName() + " bean!");
      }
    }
  }

  private String generateBeanName(String simpleClassName) {
    return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
  }
}
