package team.zavod.di.CyclicDependency;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

public class TestCyclicDependency {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private final BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.CyclicDependency"}, this.classLoader);

  @Test
  void test() {
    BeanA beanA = beanFactory.getBean(BeanA.class);
    BeanB beanB = beanFactory.getBean(BeanB.class);
    Assertions.assertSame(beanA, beanB.getBeanA());
    Assertions.assertSame(beanB, beanA.getBeanB());
  }
}
