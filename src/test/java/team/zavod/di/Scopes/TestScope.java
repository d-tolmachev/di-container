package team.zavod.di.Scopes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

public class TestScope {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
  private final BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.Scopes"}, this.classLoader);

  @Test
  void testJakartaSingleton() {
    JakartaSingletonBean A = beanFactory.getBean(JakartaSingletonBean.class);
    JakartaSingletonBean B = beanFactory.getBean(JakartaSingletonBean.class);
    Assertions.assertSame(A, B);
  }

  @Test
  void testScopeSingleton() {
    ScopeSingletonBean A = beanFactory.getBean(ScopeSingletonBean.class);
    ScopeSingletonBean B = beanFactory.getBean(ScopeSingletonBean.class);
    Assertions.assertSame(A, B);
  }

  @Test
  void testScopeSingleThread() {
    ScopeThreadBean A = beanFactory.getBean(ScopeThreadBean.class);
    ScopeThreadBean B = beanFactory.getBean(ScopeThreadBean.class);
    Assertions.assertSame(A, B);
  }

  @Test
  void testScopeMultiThread() throws InterruptedException {
    TestThreadInsideSingletonGenerator threadA = new TestThreadInsideSingletonGenerator();
    TestThreadInsideSingletonGenerator threadB = new TestThreadInsideSingletonGenerator();
    threadA.start();
    threadB.start();
    threadA.join();
    threadB.join();

    Assertions.assertNotSame(threadA.getCreatedBean(), threadB.getCreatedBean());
  }

  @Test
  void testScopePrototype() {
    ScopePrototypeBean A = beanFactory.getBean(ScopePrototypeBean.class);
    ScopePrototypeBean B = beanFactory.getBean(ScopePrototypeBean.class);
    Assertions.assertNotSame(A, B);
  }

  class TestThreadInsideSingletonGenerator extends Thread {
    private final BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.Scopes"});
    private ScopeSingletonBean resultBean;
    @Override
    public void run() {
      resultBean = beanFactory.getBean(ScopeSingletonBean.class);
    }

    public ScopeSingletonBean getCreatedBean() {
      return this.resultBean;
    }
  }
}
