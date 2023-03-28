package team.zavod.di.AutowiredPrimary;

import jakarta.inject.Named;
import team.zavod.di.AutowiredPrimary.interfaces.PaymentSystem;
import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;

@Component
public class Service {
  private PaymentSystem paymentSystem;

  @Autowired
  public Service(PaymentSystem paymentSystem) {
    this.paymentSystem = paymentSystem;
  }

  public void run() {
    this.paymentSystem.pay();
  }
}
