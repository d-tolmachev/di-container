package team.zavod.di.AutowiredWithSingleImplementation;

import team.zavod.di.AutowiredWithSingleImplementation.impl.CardPaymentSystem;
import team.zavod.di.annotation.BasePackages;
import team.zavod.di.annotation.Bean;
import team.zavod.di.annotation.Configuration;

@Configuration
@BasePackages("team.zavod.di.AutowiredWithSingleImplementation")
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
