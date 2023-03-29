package team.zavod.di.AutowiredWithMultipleImplementations;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Named;
import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.AutowiredWithMultipleImplementations.interfaces.PaymentSystem;
import team.zavod.di.annotation.Lazy;

@Component
@Lazy
public class Service {
  private PaymentSystem paymentSystem;

  @Autowired
  public Service(@Named("cardPaymentSystem") PaymentSystem paymentSystem) {
    this.paymentSystem = paymentSystem;
  }

  public void run() {
    this.paymentSystem.pay();
  }

  @PostConstruct
  public void init() {
    System.out.println("Initializing service...");
  }

  @PreDestroy
  public void destroy() {
    System.out.println("Destroying service...");
  }
}
