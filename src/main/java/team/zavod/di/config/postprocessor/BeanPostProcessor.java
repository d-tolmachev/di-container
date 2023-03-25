package team.zavod.di.config.postprocessor;

import team.zavod.di.exception.BeanException;

@SuppressWarnings("unused")
public interface BeanPostProcessor {
  default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanException {
    return bean;
  }

  default Object postProcessAfterInitialization(Object bean, String beanName) throws BeanException {
    return bean;
  }
}
