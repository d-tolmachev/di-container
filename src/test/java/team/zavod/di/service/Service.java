package team.zavod.di.service;

import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.service.interfaces.PaymentSystem;

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
