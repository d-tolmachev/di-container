package team.zavod.di.config.postprocessor;

import team.zavod.di.exception.BeanException;

public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {
  void postProcessBeforeDestruction(Object bean, String beanName) throws BeanException;
}
