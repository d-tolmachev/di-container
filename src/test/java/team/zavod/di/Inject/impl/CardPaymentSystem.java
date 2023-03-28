package team.zavod.di.Inject.impl;

import team.zavod.di.Inject.interfaces.PaymentSystem;
import team.zavod.di.annotation.Component;

import java.util.logging.Logger;

@Component
public class CardPaymentSystem implements PaymentSystem {
  Logger LOG = Logger.getLogger(CardPaymentSystem.class.getName());
  @Override
  public void pay() {
    LOG.info("Payed by CARD");
  }
}
