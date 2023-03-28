package team.zavod.di.AutowiredPrimary;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import team.zavod.di.AutowiredPrimary.impl.CardPaymentSystem;
import team.zavod.di.AutowiredPrimary.interfaces.PaymentSystem;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

import java.lang.reflect.Field;

public class TestAutowiredPrimary {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @Test
  void testAnnotationConfigurationFactory() throws NoSuchFieldException, IllegalAccessException {
    BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.AutowiredPrimary"}, this.classLoader);
    Service service = beanFactory.getBean("service", Service.class);
    service.run();
    checkServiceHasCorrectInstanse(service);
  }

  void checkServiceHasCorrectInstanse(Service service) throws NoSuchFieldException, IllegalAccessException {
    Field paymentSystemField = Service.class.getDeclaredField("paymentSystem");
    paymentSystemField.setAccessible(true);
    PaymentSystem paymentSystemImpl = (PaymentSystem) paymentSystemField.get(service);
    Assertions.assertInstanceOf(CardPaymentSystem.class, paymentSystemImpl);
  }
}
