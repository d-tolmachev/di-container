package team.zavod.di.CyclicDependency;

import jakarta.inject.Inject;
import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Lazy;

@Component
@Lazy
public class BeanB {
  @Inject
  private BeanA beanA;

  public BeanA getBeanA() {
    return this.beanA;
  }
}
