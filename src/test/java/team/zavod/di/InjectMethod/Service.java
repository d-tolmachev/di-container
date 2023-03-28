package team.zavod.di.InjectMethod;

import jakarta.inject.Inject;
import team.zavod.di.InjectMethod.interfaces.PaymentSystem;
import team.zavod.di.annotation.Component;

@Component
public class Service {
  private PaymentSystem paymentSystem;

  public void run() {
    this.paymentSystem.pay();
  }

  @Inject
  public void setPaymentSystem(PaymentSystem paymentSystem) {
    this.paymentSystem = paymentSystem;
  }
}
