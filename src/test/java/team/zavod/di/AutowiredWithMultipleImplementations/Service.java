package team.zavod.di.AutowiredWithMultipleImplementations;

import jakarta.inject.Named;
import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.AutowiredWithMultipleImplementations.interfaces.PaymentSystem;

@Component
public class Service {
  private PaymentSystem paymentSystem;

  @Autowired
  public Service(@Named("cardPaymentSystem") PaymentSystem paymentSystem) {
    this.paymentSystem = paymentSystem;
  }

  public void run() {
    this.paymentSystem.pay();
  }
}
