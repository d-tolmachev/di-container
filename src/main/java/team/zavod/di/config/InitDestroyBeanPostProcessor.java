package team.zavod.di.config;

import java.lang.annotation.Annotation;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import team.zavod.di.exception.BeanException;

public class InitDestroyBeanPostProcessor implements DestructionAwareBeanPostProcessor {
  private Class<? extends Annotation> initAnnotationType;
  private Class<? extends Annotation> destroyAnnotationType;

  public InitDestroyBeanPostProcessor() {
    this.initAnnotationType = PostConstruct.class;
    this.destroyAnnotationType = PreDestroy.class;
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
    return null;  // TODO
  }

  @Override
  public void postProcessBeforeDestruction(Object bean, String beanName) throws BeanException {
  }
}
