package team.zavod.di.AutowiredWithMultipleImplementations;

import team.zavod.di.annotation.BasePackages;
import team.zavod.di.annotation.Bean;
import team.zavod.di.annotation.Configuration;
import team.zavod.di.AutowiredWithMultipleImplementations.impl.CardPaymentSystem;

@Configuration
@BasePackages("team.zavod.di.AutowiredWithMultipleImplementations")
public class ServiceConfig {
  @Bean
  public Service service() {
    return new Service(paymentSystem());
  }

  @Bean
  public CardPaymentSystem paymentSystem() {
    return new CardPaymentSystem();
  }
}
