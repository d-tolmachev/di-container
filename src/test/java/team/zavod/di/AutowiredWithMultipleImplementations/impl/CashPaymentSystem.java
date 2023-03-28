package team.zavod.di.AutowiredWithMultipleImplementations.impl;

import team.zavod.di.annotation.Component;
import team.zavod.di.AutowiredWithMultipleImplementations.interfaces.PaymentSystem;

import java.util.logging.Logger;

@Component
public class CashPaymentSystem implements PaymentSystem {
  Logger LOG = java.util.logging.Logger.getLogger(CardPaymentSystem.class.getName());
  @Override
  public void pay() {
    LOG.info("Payed by CASH");
  }
}
