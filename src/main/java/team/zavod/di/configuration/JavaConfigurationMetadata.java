package team.zavod.di.configuration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import team.zavod.di.annotation.Bean;
import team.zavod.di.annotation.BasePackages;
import team.zavod.di.annotation.Configuration;
import team.zavod.di.annotation.Lazy;
import team.zavod.di.annotation.Primary;
import team.zavod.di.annotation.Scope;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.GenericBeanDefinition;
import team.zavod.di.configuration.exception.JavaConfigurationException;
import team.zavod.di.factory.BeanDefinitionRegistry;
import team.zavod.di.factory.SimpleBeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class JavaConfigurationMetadata implements ConfigurationMetadata {
  private static final Class<Configuration> CONFIGURATION_ANNOTATION = Configuration.class;
  private static final Class<BasePackages> BASE_PACKAGES_ANNOTATION = BasePackages.class;
  private static final Class<Bean> BEAN_ANNOTATION = Bean.class;
  private static final Class<Scope> SCOPE_ANNOTATION = Scope.class;
  private static final Class<Lazy> LAZY_ANNOTATION = Lazy.class;
  private static final Class<Primary> PRIMARY_ANNOTATION = Primary.class;
  private final List<String> packagesToScan;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private ClasspathHelper classpathHelper;

  public JavaConfigurationMetadata(Class<?> configurationClass) {
    this.packagesToScan = new ArrayList<>();
    this.beanDefinitionRegistry = new SimpleBeanDefinitionRegistry();
    parseJavaConfiguration(configurationClass);
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

  private void parseJavaConfiguration(Class<?> configurationClass) {
    parseBasePackages(configurationClass);
    this.classpathHelper = new ClasspathHelper(this.packagesToScan);
    parseConfigurationClasses(this.classpathHelper.getTypesAnnotatedWith(CONFIGURATION_ANNOTATION));
  }

  private void parseBasePackages(Class<?> configurationClass) {
    if (!configurationClass.isAnnotationPresent(CONFIGURATION_ANNOTATION)) {
      throw new JavaConfigurationException("Error! Invalid configuration class!");
    }
    if (!configurationClass.isAnnotationPresent(BASE_PACKAGES_ANNOTATION)) {
      throw new JavaConfigurationException("Error! Failed to find @" + BASE_PACKAGES_ANNOTATION.getName() + " annotation!");
    }
    List<String> basePackages = Arrays.asList(configurationClass.getAnnotation(BASE_PACKAGES_ANNOTATION).value());
    if (!basePackages.isEmpty()) {
      this.packagesToScan.addAll(basePackages);
    } else if (!configurationClass.getPackageName().isEmpty()) {
      this.packagesToScan.add(configurationClass.getPackageName());
    } else throw new JavaConfigurationException("Error! Failed to find base packages specification!");
  }

  private void parseConfigurationClasses(Set<Class<?>> configurationClasses) {
    for (Class<?> configurationClass : configurationClasses) {
      Configuration configuration = configurationClass.getAnnotation(CONFIGURATION_ANNOTATION);
      String beanName = !configuration.value().isEmpty() ? configuration.value() : generateBeanName(configurationClass.getSimpleName());
      BeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanName(beanName);
      beanDefinition.setBeanClassName(configurationClass.getName());
      parseBeans(Arrays.stream(configurationClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(BEAN_ANNOTATION)).toList());
      this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
    }
  }

  @SuppressWarnings("DuplicatedCode")
  private void parseBeans(List<Method> methods) {
    for (Method method : methods) {
      Bean bean = method.getAnnotation(BEAN_ANNOTATION);
      String beanName = !bean.name().isEmpty() ? bean.name() : method.getName();
      BeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanName(beanName);
      beanDefinition.setBeanClassName(method.getReturnType().getName());
      if (method.isAnnotationPresent(SCOPE_ANNOTATION)) {
        Scope scope = method.getAnnotation(SCOPE_ANNOTATION);
        beanDefinition.setScope(scope.value());
      }
      if (method.isAnnotationPresent(LAZY_ANNOTATION)) {
        Lazy lazy = method.getAnnotation(LAZY_ANNOTATION);
        beanDefinition.setLazyInit(lazy.value());
      }
      if (method.isAnnotationPresent(PRIMARY_ANNOTATION)) {
        Primary primary = method.getAnnotation(PRIMARY_ANNOTATION);
        beanDefinition.setPrimary(primary.value());
      }
      beanDefinition.setFactoryBeanName(method.getDeclaringClass().getName());
      beanDefinition.setFactoryMethodName(method.getName());
      this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
    }
  }

  private String generateBeanName(String simpleClassName) {
    return simpleClassName.substring(0, 1).toLowerCase() + simpleClassName.substring(1);
  }
}
