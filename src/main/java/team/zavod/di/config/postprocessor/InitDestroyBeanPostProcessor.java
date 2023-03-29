package team.zavod.di.config.postprocessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import team.zavod.di.config.BeanDefinition;
import team.zavod.di.configuration.BeanConfigurator;
import team.zavod.di.exception.BeanException;
import team.zavod.di.factory.registry.BeanDefinitionRegistry;
import team.zavod.di.util.ClasspathHelper;

public class InitDestroyBeanPostProcessor implements DestructionAwareBeanPostProcessor {
  private final ClasspathHelper classpathHelper;
  private final BeanDefinitionRegistry beanDefinitionRegistry;

  public InitDestroyBeanPostProcessor(BeanConfigurator beanConfigurator) {
    this.classpathHelper = beanConfigurator.getConfigurationMetadata().getClasspathHelper();
    this.beanDefinitionRegistry = beanConfigurator.getConfigurationMetadata().getBeanDefinitionRegistry();
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
    BeanDefinition beanDefinition = this.beanDefinitionRegistry.getBeanDefinition(beanName);
    Class<?> beanClass = this.classpathHelper.classForName(beanDefinition.getBeanClassName());
    try {
      if (Objects.nonNull(beanDefinition.getInitMethodName())) {
        Method initMethod = beanClass.getDeclaredMethod(beanDefinition.getInitMethodName());
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
    try {
      if (Objects.nonNull(beanDefinition.getDestroyMethodName())) {
        Method destroyMethod = beanClass.getDeclaredMethod(beanDefinition.getDestroyMethodName());
        destroyMethod.setAccessible(true);
        destroyMethod.invoke(bean);
      }
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new BeanException("Error! Failed to invoke destroyBeanPostProcessor for " + beanName + " bean!");
    }
  }
}
