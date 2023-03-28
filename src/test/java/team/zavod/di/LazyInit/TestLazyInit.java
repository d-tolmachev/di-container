package team.zavod.di.LazyInit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

public class TestLazyInit {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private final BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.LazyInit"}, this.classLoader);

  @Test
  void testLazyBean() {
    Assertions.assertFalse(beanFactory.containsBean("lazyBean"));
    LazyBean lazyBean = beanFactory.getBean(LazyBean.class);
    Assertions.assertTrue(beanFactory.containsBean("lazyBean"));
  }

  @Test
  void testNonLazyBean() {
    Assertions.assertTrue(beanFactory.containsBean("nonLazyBean"));
    NonLazyBean nonLazyBean = beanFactory.getBean(NonLazyBean.class);
    Assertions.assertTrue(beanFactory.containsBean("nonLazyBean"));
  }
}
