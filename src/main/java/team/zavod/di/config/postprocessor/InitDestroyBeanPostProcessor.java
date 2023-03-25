package team.zavod.di.config.postprocessor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class InitDestroyBeanPostProcessor implements DestructionAwareBeanPostProcessor {
  private static final Class<? extends Annotation> DEFAULT_INIT_ANNOTATION_TYPE = PostConstruct.class;
  private static final Class<? extends Annotation> DEFAULT_DESTROY_ANNOTATION_TYPE = PreDestroy.class;
  private final BeanConfigurator beanConfigurator;
  private final ClasspathHelper classpathHelper;
  private final BeanDefinitionRegistry beanDefinitionRegistry;
  private Class<? extends Annotation> initAnnotationType;
  private Class<? extends Annotation> destroyAnnotationType;

  public InitDestroyBeanPostProcessor(BeanConfigurator beanConfigurator) {
    this.beanConfigurator = beanConfigurator;
    this.classpathHelper = this.beanConfigurator.getConfigurationMetadata().getClasspathHelper();
    this.beanDefinitionRegistry = this.beanConfigurator.getConfigurationMetadata().getBeanDefinitionRegistry();
    this.initAnnotationType = DEFAULT_INIT_ANNOTATION_TYPE;
    this.destroyAnnotationType = DEFAULT_DESTROY_ANNOTATION_TYPE;
  }

  public Class<? extends Annotation> getInitAnnotationType() {
    return this.initAnnotationType;
  }

  public void setInitAnnotationType(Class<? extends Annotation> initAnnotationType) {
    this.initAnnotationType = initAnnotationType;
  }

  public Class<? extends Annotation> getDestroyAnnotationType() {
    return this.destroyAnnotationType;
  }

  public void setDestroyAnnotationType(Class<? extends Annotation> destroyAnnotationType) {
    this.destroyAnnotationType = destroyAnnotationType;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
    BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
    Class<?> beanClass = this.classpathHelper.classForName(beanDefinition.getBeanClassName());
    if (beanClass.isInterface()) {
      beanClass = this.beanConfigurator.getImplementationClass(beanClass);
    }
    try {
      Set<Method> initMethods = new HashSet<>();
      if (Objects.nonNull(beanDefinition.getInitMethodName())) {
        initMethods.add(beanClass.getDeclaredMethod(beanDefinition.getInitMethodName()));
      }
      initMethods.addAll(Arrays.stream(beanClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(this.initAnnotationType)).collect(Collectors.toSet()));
      for (Method initMethod : initMethods) {
        initMethod.setAccessible(true);
        initMethod.invoke(bean);
      }
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to invoke initBeanPostProcessor for " + beanName + " bean!");
    }
    return bean;
  }

  @Override
  public void postProcessBeforeDestruction(Object bean, String beanName) throws BeanException {
    BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
    Class<?> beanClass = this.classpathHelper.classForName(beanDefinition.getBeanClassName());
    if (beanClass.isInterface()) {
      beanClass = this.beanConfigurator.getImplementationClass(beanClass);
    }
    try {
      Set<Method> destroyMethods = new HashSet<>();
      if (Objects.nonNull(beanDefinition.getInitMethodName())) {
        destroyMethods.add(beanClass.getDeclaredMethod(beanDefinition.getDestroyMethodName()));
      }
      destroyMethods.addAll(Arrays.stream(beanClass.getDeclaredMethods()).filter(method -> method.isAnnotationPresent(this.destroyAnnotationType)).collect(Collectors.toSet()));
      for (Method destroyMethod : destroyMethods) {
        destroyMethod.setAccessible(true);
        destroyMethod.invoke(bean);
      }
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to invoke destroyBeanPostProcessor for " + beanName + " bean!");
    }
  }
}
