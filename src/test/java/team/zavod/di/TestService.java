package team.zavod.di;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import team.zavod.di.factory.BeanFactory;
import team.zavod.di.factory.DefaultBeanFactory;
import team.zavod.di.service.Service;
import team.zavod.di.service.ServiceConfig;

public class TestService {
    private final ClassLoader classLoader;

    TestService() {
        this.classLoader = Thread.currentThread().getContextClassLoader().getParent();
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Test
    @DisplayName("Annotation configuration factory")
    void annotationConfigurationFactory_Test() {
        BeanFactory beanFactory = new DefaultBeanFactory(new String[]{"team.zavod.di.service"}, this.classLoader);
        Service service = beanFactory.getBean("service", Service.class);
        service.run();
    }

    @Test
    @DisplayName("Java configuration factory")
    void javaConfigurationFactory_Test() {
        BeanFactory beanFactory = new DefaultBeanFactory(ServiceConfig.class, this.classLoader);
        Service service = beanFactory.getBean("service", Service.class);
        service.run();
    }

    @Test
    @DisplayName("Xml configuration factory")
    void xmlConfigurationFactory_Test() {
        BeanFactory beanFactory = new DefaultBeanFactory("test/beans.xml", this.classLoader);
        Service service = beanFactory.getBean("service", Service.class);
        service.run();
    }
}
