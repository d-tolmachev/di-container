package team.zavod.di.AutowiredWithSingleImplementation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.zavod.di.AutowiredWithSingleImplementation.impl.CardPaymentSystem;
import team.zavod.di.AutowiredWithSingleImplementation.interfaces.PaymentSystem;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;

import java.lang.reflect.Field;

public class TestAutowiredWithSingleImplementation {
  private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  @DisplayName("Annotation configuration factory")
  void testAnnotationConfigurationFactory() throws NoSuchFieldException, IllegalAccessException {
    BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.AutowiredWithSingleImplementation"}, this.classLoader);
    Service service = beanFactory.getBean("service", Service.class);
    service.run();
    checkServiceHasCorrectInstanse(service);
  }

  @Test
  @DisplayName("Java configuration factory")
  void testJavaConfigurationFactory() throws NoSuchFieldException, IllegalAccessException {
    BeanFactory beanFactory = new DefaultBeanFactory(ServiceConfig.class, this.classLoader);
    Service service = beanFactory.getBean("service", Service.class);
    service.run();
    checkServiceHasCorrectInstanse(service);
  }

  @Test
  @DisplayName("Xml configuration factory")
  void testXmlConfigurationFactory() throws NoSuchFieldException, IllegalAccessException {
    BeanFactory beanFactory = new DefaultBeanFactory(classLoader.getResourceAsStream("AutowiredWithSingleImplementation.xml"), this.classLoader);
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
