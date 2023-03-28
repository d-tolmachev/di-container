package team.zavod.di.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.zavod.di.Inject.impl.CardPaymentSystem;
import team.zavod.di.Inject.interfaces.PaymentSystem;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

import java.lang.reflect.Field;

public class TestInject {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void testAnnotationConfigurationFactory() throws NoSuchFieldException, IllegalAccessException {
    BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.Inject"}, this.classLoader);
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
