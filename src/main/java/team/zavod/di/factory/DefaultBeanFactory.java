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
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.config.dependency.ArgumentValues;
import team.zavod.di.config.dependency.MethodArgumentValues;
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
import team.zavod.di.factory.exception.BeanNotOfRequiredTypeException;
import team.zavod.di.factory.exception.NoSuchBeanDefinitionException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class DefaultBeanFactory implements BeanFactory {
  private final Map<String, Set<BeanDefinition>> beanProviders;
  private final Set<BeanDefinition> dependencyContext;
  private final Map<String, Class<?>> classesForNames;
  private final BeanConfigurator beanConfigurator;
  private final ClasspathHelper classpathHelper;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private final List<BeanPostProcessor> beanPostProcessors;
  private final Map<String, Scope> scopes;

  private DefaultBeanFactory(ConfigurationMetadata configurationMetadata) {
    this.beanProviders = new HashMap<>();
    this.dependencyContext = new HashSet<>();
    this.classesForNames = new HashMap<>();
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
    Class<? extends T> beanClass = requiredType.isInterface() ? this.beanConfigurator.getImplementationClass(requiredType) : requiredType;
    return getBean(getBeanDefinition(beanClass.getName()).getBeanName(), requiredType);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T getBean(String name, Class<T> requiredType) throws BeanException {
    BeanDefinition beanDefinition = getBeanDefinition(name);
    if (!this.scopes.containsKey(beanDefinition.getScope())) {
      throw new BeanException("Error! Failed to find " + beanDefinition.getScope() + " scope for " + beanDefinition.getBeanName() + " bean!");
    }
    Class<? extends T> beanClass = (Class<? extends T>) getClassForName(beanDefinition);
    if (!requiredType.isAssignableFrom(beanClass)) {
      throw new BeanNotOfRequiredTypeException("Error! Failed to instantiate " + beanDefinition.getBeanName() + " bean as instance of " + requiredType.getName() + " class!");
    }
    if (!this.dependencyContext.add(beanDefinition)) {
      throw new BeanException("Error! Failed to instantiate " + beanDefinition.getBeanName() + " bean due to circular dependencies!");
    }
    T bean = (T) getScope(beanDefinition.getScope()).get(name, requiredType, getProvider(beanDefinition, beanClass));
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
  public List<String> getBeanNames() {
    return this.beanDefinitionRegistry.getBeanNames();
  }

  private void initializeBeans() {
    for (String beanName : getBeanNames()) {
      BeanDefinition beanDefinition = getBeanDefinition(beanName);
      if (Objects.nonNull(beanDefinition.getFactoryBeanName())) {
        this.beanProviders.computeIfAbsent(beanDefinition.getFactoryBeanName(), k -> new HashSet<>()).add(beanDefinition);
      }
      if (!beanDefinition.isLazyInit() && beanDefinition.isSingleton()) {
        getBean(beanDefinition.getBeanName(), this.classpathHelper.classForName(beanDefinition.getBeanClassName()));
      }
    }
  }

  private void addDefaultBeanPostProcessors() {
    addBeanPostProcessor(new InitDestroyBeanPostProcessor(this.beanConfigurator));
  }

  private void registerDefaultScopes() {
    this.scopes.put(SingletonScope.getName(), new SingletonScope());
    this.scopes.put(PrototypeScope.getName(), new PrototypeScope());
    this.scopes.put(ThreadScope.getName(), new ThreadScope());
  }

  private Class<?> getClassForName(BeanDefinition beanDefinition) {
    if (!this.classesForNames.containsKey(beanDefinition.getBeanClassName())) {
      this.classesForNames.put(beanDefinition.getBeanClassName(), generateBeanClass(beanDefinition, this.classpathHelper.classForName(beanDefinition.getBeanClassName())));
    }
    return this.classesForNames.get(beanDefinition.getBeanClassName());
  }

  private <T> ObjectProvider<T> getProvider(BeanDefinition beanDefinition, Class<? extends T> beanClass) {
    return () -> Objects.isNull(beanDefinition.getFactoryBeanName()) ? constructBean(beanDefinition, beanClass) : provideBean(beanDefinition);
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
      this.beanPostProcessors.forEach(beanPostProcessor -> beanPostProcessor.postProcessAfterInitialization(bean, beanDefinition.getBeanName()));
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

  @SuppressWarnings("resource")
  private Class<?> generateBeanClass(BeanDefinition beanDefinition, Class<?> originClass) {
    Map<Method, MethodArgumentValues> methods = getBeanMethods(Arrays.asList(originClass.getDeclaredMethods()), beanDefinition);
    Builder<?> builder = new ByteBuddy().subclass(originClass);
    for (BeanDefinition providerBeanDefinition : this.beanProviders.getOrDefault(beanDefinition.getBeanName(), Set.of())) {
      builder = builder.method(ElementMatchers.named(providerBeanDefinition.getFactoryMethodName())
          .and(ElementMatchers.takesNoArguments()))
          .intercept(MethodCall.call(() -> getBean(providerBeanDefinition.getBeanName(), this.classpathHelper.classForName(providerBeanDefinition.getBeanClassName()))));
    }
    for (Entry<Method, MethodArgumentValues> methodEntry : methods.entrySet()) {
      List<ObjectProvider<?>> argumentProviders = getBeanDependencies(methodEntry.getKey().getParameters(), methodEntry.getValue());
      builder = builder.method(ElementMatchers.is(methodEntry.getKey()))
          .intercept(MethodCall.invokeSuper()
              .with(argumentProviders.stream().map(ObjectProvider::getObject).toArray(Object[]::new)));
    }
    return builder.make().load(Thread.currentThread().getContextClassLoader()).getLoaded();
  }

  @SuppressWarnings("unchecked")
  private <T> T constructBean(BeanDefinition beanDefinition, Class<? extends T> beanClass) {
    try {
      Constructor<? extends T> constructor = (Constructor<? extends T>) getBeanConstructor(Arrays.asList(beanClass.getDeclaredConstructors()), beanDefinition.getConstructorArgumentValues());
      List<ObjectProvider<?>> initArgProviders = getBeanDependencies(constructor.getParameters(), beanDefinition.getConstructorArgumentValues());
      constructor.setAccessible(true);
      return constructor.newInstance(initArgProviders.stream().map(ObjectProvider::getObject).toArray(Object[]::new));
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to instantiate " + beanDefinition.getBeanName() + " bean!");
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T provideBean(BeanDefinition beanDefinition) {
    try {
      Class<?> factoryBeanClass = this.classpathHelper.classForName(getBeanDefinition(beanDefinition.getFactoryBeanName()).getBeanClassName());
      Object factoryBean = getBean(beanDefinition.getFactoryBeanName(), factoryBeanClass);
      Method factoryMethod = factoryBeanClass.getDeclaredMethod(beanDefinition.getFactoryMethodName());
      factoryMethod.setAccessible(true);
      return (T) factoryMethod.invoke(factoryBean);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to invoke " + beanDefinition.getFactoryBeanName() + "." + beanDefinition.getFactoryMethodName() + " as factory method!");
    }
  }

  private Map<Method, MethodArgumentValues> getBeanMethods(List<Method> methods, BeanDefinition beanDefinition) {
    Map<Method, MethodArgumentValues> methodCandidates = new HashMap<>();
    for (Method method : methods) {
      if (beanDefinition.hasMethodArgumentValues(method.getName())) {
        for (MethodArgumentValues methodArgumentValues : beanDefinition.getMethodArgumentValues(method.getName())) {
          if (isBeanArguments(method.getParameters(), methodArgumentValues)) {
            methodCandidates.put(method, methodArgumentValues);
          }
        }
      }
    }
    return methodCandidates;
  }

  private <T> Constructor<? extends T> getBeanConstructor(List<Constructor<? extends T>> constructors, ConstructorArgumentValues constructorArgumentValues) {
    List<Constructor<? extends T>> constructorCandidates = constructors.stream().filter(constructor -> isBeanArguments(constructor.getParameters(), constructorArgumentValues)).toList();
    if (constructorCandidates.size() > 1) {
      throw new BeanException("Error! Failed to unambiguously determine constructor for bean!");
    } else if (!constructorCandidates.isEmpty()) {
      return constructorCandidates.get(0);
    } else throw new BeanException("Error! Failed to find constructor for bean!");
  }

  private Map<Field, ObjectProvider<?>> getBeanInitializationDependencies(Map<String, Field> fields, PropertyValues propertyValues) {
    Map<Field, ObjectProvider<?>> beanDependencies = new HashMap<>();
    for (ValueHolder valueHolder : propertyValues.getGenericValues()) {
      if (!fields.containsKey(valueHolder.getName())) {
        throw new BeanException("Error! Failed to find " + valueHolder.getName() + " field!");
      }
      processValueHolder(valueHolder);
      beanDependencies.put(fields.get(valueHolder.getName()), valueHolder.getValueProvider());
    }
    return beanDependencies;
  }

  private List<ObjectProvider<?>> getBeanDependencies(Parameter[] parameters, ArgumentValues argumentValues) {
    List<ObjectProvider<?>> beanDependencies = new ArrayList<>(parameters.length);
    for (int i = 0; i < parameters.length; i++) {
      ValueHolder valueHolder = parameters[i].isNamePresent() ? argumentValues.getGenericValue(parameters[i].getName()) : argumentValues.getIndexedValue(i);
      processValueHolder(valueHolder);
      beanDependencies.add(valueHolder.getValueProvider());
    }
    return beanDependencies;
  }

  private boolean isBeanArguments(Parameter[] parameters, ArgumentValues argumentValues) {
    if (argumentValues.size() != parameters.length) {
      return false;
    }
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isNamePresent()) {
        if (!argumentValues.hasGenericValue(parameters[i].getName()) || !parameters[i].getType().isAssignableFrom(this.classpathHelper.classForName(argumentValues.getGenericValue(parameters[i].getName()).getType()))) {
          return false;
        }
      } else if (!argumentValues.hasIndexedValue(i) || !parameters[i].getType().isAssignableFrom(this.classpathHelper.classForName(argumentValues.getIndexedValue(i).getType()))) {
        return false;
      }
    }
    return true;
  }

  private void processValueHolder(ValueHolder valueHolder) {
    if (Objects.isNull(valueHolder.getBeanName())) {
      Class<?> beanClass = this.classpathHelper.classForName(valueHolder.getType());
      if (beanClass.isInterface()) {
        beanClass = this.beanConfigurator.getImplementationClass(beanClass);
      }
      valueHolder.setBeanName(getBeanDefinition(beanClass.getName()).getBeanName());
    }
    if (Objects.isNull(valueHolder.getValueProvider())) {
      valueHolder.setValueProvider(getBeanProvider(valueHolder.getBeanName(), this.classpathHelper.classForName(getBeanDefinition(valueHolder.getBeanName()).getBeanClassName())));
    }
  }
}
