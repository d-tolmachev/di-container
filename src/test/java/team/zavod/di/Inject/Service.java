package team.zavod.di.Inject;

import jakarta.inject.Inject;
import team.zavod.di.Inject.interfaces.PaymentSystem;
import team.zavod.di.annotation.Component;

@Component
public class Service {
  @Inject
  private PaymentSystem paymentSystem;

  public void run() {
    this.paymentSystem.pay();
  }
}
