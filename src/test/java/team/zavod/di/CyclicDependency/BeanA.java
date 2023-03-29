package team.zavod.di.CyclicDependency;

import jakarta.inject.Inject;
import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Lazy;

@Component
public class BeanA {
  @Inject
  private BeanB beanB;

  public BeanB getBeanB() {
    return this.beanB;
  }
}
