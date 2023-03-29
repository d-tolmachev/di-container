package team.zavod.di.AutowiredWithMultipleImplementations;

import jakarta.annotation.PostConstruct;
import team.zavod.di.annotation.BasePackages;
import team.zavod.di.annotation.Bean;
import team.zavod.di.annotation.Configuration;
import team.zavod.di.AutowiredWithMultipleImplementations.impl.CardPaymentSystem;
import team.zavod.di.annotation.Lazy;

@Configuration
@BasePackages("team.zavod.di.AutowiredWithMultipleImplementations")
public class ServiceConfig {
  @Bean
  @Lazy
  public Service service() {
    return new Service(paymentSystem());
  }

  @Bean
  public CardPaymentSystem paymentSystem() {
    return new CardPaymentSystem();
  }
}
