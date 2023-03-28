package team.zavod.di.AutowiredPrimary.impl;

import team.zavod.di.AutowiredPrimary.interfaces.PaymentSystem;
import team.zavod.di.annotation.Component;
import team.zavod.di.annotation.Primary;

import java.util.logging.Logger;

@Component
@Primary
public class CardPaymentSystem implements PaymentSystem {
  Logger LOG = Logger.getLogger(CardPaymentSystem.class.getName());
  @Override
  public void pay() {
    LOG.info("Payed by CARD");
  }
}
