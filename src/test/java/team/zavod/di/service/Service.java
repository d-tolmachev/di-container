package team.zavod.di.service;

import jakarta.inject.Inject;
import team.zavod.di.configuration.metadata.JavaConfigurationMetadata;
import team.zavod.di.factory.DefaultBeanFactory;
import team.zavod.di.service.interfaces.PaymentSystem;

public class Service {
    private PaymentSystem paymentSystem = new DefaultBeanFactory(new JavaConfigurationMetadata(ServiceConfig.class)).getBean(PaymentSystem.class);

    public void run() {
        paymentSystem.pay();
    }
}
