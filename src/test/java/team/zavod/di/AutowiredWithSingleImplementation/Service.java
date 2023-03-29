package team.zavod.di.AutowiredWithSingleImplementation;

import team.zavod.di.AutowiredWithSingleImplementation.interfaces.PaymentSystem;
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
