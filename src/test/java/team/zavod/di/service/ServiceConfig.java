package team.zavod.di.service;

import team.zavod.di.annotation.BasePackages;
import team.zavod.di.annotation.Bean;
import team.zavod.di.annotation.Configuration;
import team.zavod.di.service.impl.CardPaymentSystem;
import team.zavod.di.service.interfaces.PaymentSystem;

@Configuration
@BasePackages("team.zavod.di.service")
public class ServiceConfig {
  @Bean
  public Service service() {
    return new Service(paymentSystem());
  }

  @Bean
  public PaymentSystem paymentSystem() {
    return new CardPaymentSystem();
  }
}
