package team.zavod.di.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.postprocessor.BeanPostProcessor;
import team.zavod.di.config.dependency.ConstructorArgumentValues;
import team.zavod.di.config.postprocessor.DestructionAwareBeanPostProcessor;
import team.zavod.di.config.postprocessor.InitDestroyBeanPostProcessor;
import team.zavod.di.config.dependency.PropertyValues;
import team.zavod.di.config.dependency.ValueHolder;
import team.zavod.di.config.scope.PrototypeScope;
import team.zavod.di.config.scope.Scope;
import team.zavod.di.config.scope.SingletonScope;
import team.zavod.di.config.scope.ThreadScope;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.configuration.metadata.AnnotationConfigurationMetadata;
import team.zavod.di.configuration.metadata.ConfigurationMetadata;
import team.zavod.di.configuration.GenericBeanConfigurator;
import team.zavod.di.configuration.metadata.JavaConfigurationMetadata;
import team.zavod.di.configuration.metadata.XmlConfigurationMetadata;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.exception.BeanDefinitionStoreException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class DefaultBeanFactory implements BeanFactory {
  private final Set<BeanDefinition> dependencyContext;
  private final BeanConfigurator beanConfigurator;
  private final ClasspathHelper classpathHelper;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private final List<BeanPostProcessor> beanPostProcessors;
  private final Map<String, Scope> scopes;

  private DefaultBeanFactory(ConfigurationMetadata configurationMetadata) {
    this.dependencyContext = new HashSet<>();
    this.beanConfigurator = new GenericBeanConfigurator(configurationMetadata);
    this.classpathHelper = this.beanConfigurator.getConfigurationMetadata().getClasspathHelper();
    this.beanDefinitionRegistry = this.beanConfigurator.getConfigurationMetadata().getBeanDefinitionRegistry();
    this.beanPostProcessors = new ArrayList<>();
    this.scopes = new HashMap<>();
    addDefaultBeanPostProcessors();
    registerDefaultScopes();
    initializeBeans();
  }

  public DefaultBeanFactory(String filename) {
    this(new XmlConfigurationMetadata(filename));
  }

  public DefaultBeanFactory(Class<?> configurationClass) {
    this(new JavaConfigurationMetadata(configurationClass));
  }

  public DefaultBeanFactory(String[] basePackages) {
    this(new AnnotationConfigurationMetadata(basePackages));
  }

  public DefaultBeanFactory(String filename, ClassLoader classLoader) {
    this(new XmlConfigurationMetadata(filename, classLoader));
  }

  public DefaultBeanFactory(Class<?> configurationClass, ClassLoader classLoader) {
    this(new JavaConfigurationMetadata(configurationClass, classLoader));
  }

  public DefaultBeanFactory(String[] basePackages, ClassLoader classLoader) {
    this(new AnnotationConfigurationMetadata(basePackages, classLoader));
  }

  @Override
  public boolean containsBean(String name) {
    BeanDefinition beanDefinition = getBeanDefinition(name);
    if (!this.scopes.containsKey(beanDefinition.getScope())) {
      throw new BeanException("Error! Failed to find " + beanDefinition.getScope() + " scope for " + name + " bean!");
    }
    try {
      return getScope(beanDefinition.getScope()).contains(name);
    } catch (UnsupportedOperationException ignored) {
      return false;
    }
  }

  @Override
  public <T> T getBean(Class<T> requiredType) throws BeanException {
    return getBean(getBeanDefinition(requiredType.getName()).getBeanName(), requiredType);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeanException {
    BeanDefinition beanDefinition = getBeanDefinition(name);
    if (!this.scopes.containsKey(beanDefinition.getScope())) {
      throw new BeanException("Error! Failed to find " + beanDefinition.getScope() + " scope for " + name + " bean!");
    }
    if (!this.dependencyContext.add(beanDefinition)) {
      throw new BeanException("Error! Failed to instantiate " + name + " bean due to circular dependencies!");
    }
    T bean = (T) getScope(beanDefinition.getScope()).get(name, requiredType, getProvider(beanDefinition));
    this.dependencyContext.remove(beanDefinition);
    processInitialization(beanDefinition, bean);
    return bean;
  }

  @Override
  public void destroyBean(String name) throws BeanException {
    BeanDefinition beanDefinition = getBeanDefinition(name);
    if (!this.scopes.containsKey(beanDefinition.getScope())) {
      throw new BeanException("Error! Failed to find " + beanDefinition.getScope() + " scope for " + name + " bean!");
    }
    try {
      Object bean = getScope(getBeanDefinition(name).getScope()).remove(name);
      if (Objects.nonNull(bean)) {
        processDestruction(beanDefinition, bean);
      }
    } catch (UnsupportedOperationException ignored) {
    }
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeanException {
    return () -> getBean(requiredType);
  }

  @Override
  public <T> ObjectProvider<T> getBeanProvider(String name, Class<T> requiredType) throws BeanException {
    return () -> getBean(name, requiredType);
  }

  @Override
  public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
    return getBeanDefinition(name).isSingleton();
  }

  @Override
  public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
    return getBeanDefinition(name).isPrototype();
  }

  @Override
  public boolean isThread(String name) throws NoSuchBeanDefinitionException {
    return getBeanDefinition(name).isThread();
  }

  @Override
  public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
    this.beanPostProcessors.add(beanPostProcessor);
  }

  @Override
  public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
    this.beanPostProcessors.addAll(beanPostProcessors);
  }

  @Override
  public List<BeanPostProcessor> getBeanPostProcessors() {
    return this.beanPostProcessors;
  }

  @Override
  public void registerScope(String scopeName, Scope scope) {
    this.scopes.put(scopeName, scope);
  }

  @Override
  public Scope getScope(String scopeName) {
    return this.scopes.get(scopeName);
  }

  @Override
  public List<String> getScopeNames() {
    return List.copyOf(this.scopes.keySet());
  }

  @Override
  public boolean containsBeanDefinition(String bean) {
    return this.beanDefinitionRegistry.containsBeanDefinition(bean);
  }

  @Override
  public void registerBeanDefinition(BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
    this.beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
  }

  @Override
  public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
    this.beanDefinitionRegistry.removeBeanDefinition(beanName);
  }

  @Override
  public BeanDefinition getBeanDefinition(String bean) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinition(bean);
  }

  @Override
  public Set<BeanDefinition> getBeanDefinitions(String beanClassName) throws NoSuchBeanDefinitionException {
    return this.beanDefinitionRegistry.getBeanDefinitions(beanClassName);
  }

  @Override
  public List<String> getBeanDefinitionNames() {
    return this.beanDefinitionRegistry.getBeanDefinitionNames();
  }

  private void initializeBeans() {
    getBeanDefinitionNames().stream()
        .map(this::getBeanDefinition)
        .filter(beanDefinition -> !beanDefinition.isLazyInit() && beanDefinition.isSingleton())
        .forEach(beanDefinition -> getBean(beanDefinition.getBeanName(), this.classpathHelper.classForName(beanDefinition.getBeanClassName())));
  }

  private void addDefaultBeanPostProcessors() {
    addBeanPostProcessor(new InitDestroyBeanPostProcessor(this.beanConfigurator));
  }

  private void registerDefaultScopes() {
    this.scopes.put(SingletonScope.getName(), new SingletonScope());
    this.scopes.put(PrototypeScope.getName(), new PrototypeScope());
    this.scopes.put(ThreadScope.getName(), new ThreadScope());
  }

  private <T> ObjectProvider<T> getProvider(BeanDefinition beanDefinition) {
    return () -> {
      T bean = Objects.isNull(beanDefinition.getFactoryBeanName()) ? constructBean(beanDefinition) : provideBean(beanDefinition);
      this.beanPostProcessors.forEach(beanPostProcessor -> beanPostProcessor.postProcessAfterInitialization(bean, beanDefinition.getBeanName()));
      return bean;
    };
  }

  private <T> void processInitialization(BeanDefinition beanDefinition, T bean) {
    try {
      Map<String, Field> fields = Arrays.stream(bean.getClass().getDeclaredFields()).collect(Collectors.toMap(Field::getName, field -> field));
      Map<Field, ObjectProvider<?>> fieldProviders = getBeanInitializationDependencies(fields, beanDefinition.getPropertyValues());
      for (Entry<Field, ObjectProvider<?>> fieldProvider : fieldProviders.entrySet()) {
        fieldProvider.getKey().setAccessible(true);
        if (Objects.isNull(fieldProvider.getKey().get(bean))) {
          fieldProvider.getKey().set(bean, fieldProvider.getValue().getObject());
        }
      }
    } catch (IllegalAccessException e) {
      throw new BeanException("Error! Failed to initialize dependencies for " + beanDefinition.getBeanName() + " bean!");
    }
  }

  private void processDestruction(BeanDefinition beanDefinition, Object bean) {
    this.beanPostProcessors.stream()
        .filter(beanPostProcessor -> beanPostProcessor.getClass().isInstance(DestructionAwareBeanPostProcessor.class))
        .map(beanPostProcessor -> (DestructionAwareBeanPostProcessor) beanPostProcessor)
        .forEach(beanPostProcessor -> beanPostProcessor.postProcessBeforeDestruction(bean, beanDefinition.getBeanName()));
  }

  @SuppressWarnings("unchecked")
  private <T> T constructBean(BeanDefinition beanDefinition) {
    Class<? extends T> beanClass = (Class<? extends T>) this.classpathHelper.classForName(beanDefinition.getBeanClassName());
    if (beanClass.isInterface()) {
      beanClass = this.beanConfigurator.getImplementationClass(beanClass);
    }
    try {
      Constructor<? extends T> constructor = (Constructor<? extends T>) getBeanConstructor(Arrays.asList(beanClass.getDeclaredConstructors()), beanDefinition.getConstructorArgumentValues());
      List<ObjectProvider<?>> initArgProviders = getBeanConstructionDependencies(constructor.getParameters(), beanDefinition.getConstructorArgumentValues());
      constructor.setAccessible(true);
      return constructor.newInstance(initArgProviders.stream().map(ObjectProvider::getObject).toArray(Object[]::new));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to instantiate " + beanDefinition.getBeanName() + " bean!");
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T provideBean(BeanDefinition beanDefinition) {
    try {
      Object factoryBean = getBean(beanDefinition.getFactoryBeanName(), this.classpathHelper.classForName(getBeanDefinition(beanDefinition.getFactoryBeanName()).getBeanClassName()));
      Method factoryMethod = factoryBean.getClass().getDeclaredMethod(beanDefinition.getFactoryMethodName());
      factoryMethod.setAccessible(true);
      return (T) factoryMethod.invoke(factoryBean);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to invoke " + beanDefinition.getFactoryBeanName() + "." + beanDefinition.getFactoryMethodName() + " as factory method!");
    }
  }

  private <T> Constructor<? extends T> getBeanConstructor(List<Constructor<? extends T>> constructors, ConstructorArgumentValues constructorArgumentValues) {
    List<Constructor<? extends T>> constructorCandidates = constructors.stream().filter(constructor -> isBeanConstructor(constructor.getParameters(), constructorArgumentValues)).toList();
    if (constructorCandidates.size() > 1) {
      throw new BeanException("Error! Failed to unambiguously determine constructor for bean!");
    } else if (!constructorCandidates.isEmpty()) {
      return constructorCandidates.get(0);
    } else throw new BeanException("Error! Failed to find constructor for bean!");
  }

  private List<ObjectProvider<?>> getBeanConstructionDependencies(Parameter[] parameters, ConstructorArgumentValues constructorArgumentValues) {
    List<ObjectProvider<?>> beanDependencies = new ArrayList<>(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      ValueHolder valueHolder = parameters[i].isNamePresent() ? constructorArgumentValues.getGenericArgumentValue(parameters[i].getName()) : constructorArgumentValues.getIndexedArgumentValue(i);
      if (Objects.isNull(valueHolder.getValueProvider())) {
        valueHolder.setValueProvider(getBeanProvider(valueHolder.getBeanName(), this.classpathHelper.classForName(getBeanDefinition(valueHolder.getBeanName()).getBeanClassName())));
      }
      beanDependencies.add(valueHolder.getValueProvider());
    }
    return beanDependencies;
  }

  private Map<Field, ObjectProvider<?>> getBeanInitializationDependencies(Map<String, Field> fields, PropertyValues propertyValues) {
    Map<Field, ObjectProvider<?>> beanDependencies = new HashMap<>();
    for (ValueHolder valueHolder : propertyValues.getPropertyValues()) {
      if (!fields.containsKey(valueHolder.getName())) {
        throw new BeanException("Error! Failed to find " + valueHolder.getName() + " field!");
      }
      if (Objects.isNull(valueHolder.getValueProvider())) {
        valueHolder.setValueProvider(getBeanProvider(valueHolder.getBeanName(), this.classpathHelper.classForName(getBeanDefinition(valueHolder.getBeanName()).getBeanClassName())));
      }
      beanDependencies.put(fields.get(valueHolder.getName()), valueHolder.getValueProvider());
    }
    return beanDependencies;
  }

  private boolean isBeanConstructor(Parameter[] parameters, ConstructorArgumentValues constructorArgumentValues) {
    if (constructorArgumentValues.size() != parameters.length) {
      return false;
    }
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isNamePresent()) {
        if (!constructorArgumentValues.hasGenericArgumentValue(parameters[i].getName()) || !constructorArgumentValues.getGenericArgumentValue(parameters[i].getName()).getType().equals(parameters[i].getType().getName())) {
          return false;
        }
      } else if (!constructorArgumentValues.hasIndexedArgumentValue(i) || !constructorArgumentValues.getIndexedArgumentValue(i).getType().equals(parameters[i].getType().getName())) {
        return false;
      }
    }
    return true;
  }
}
