package team.zavod.di.service.impl;

import team.zavod.di.annotation.Component;
import team.zavod.di.service.interfaces.PaymentSystem;

import java.util.logging.Logger;

@Component
public class CardPaymentSystem implements PaymentSystem {
    Logger LOG = java.util.logging.Logger.getLogger(CardPaymentSystem.class.getName());
    @Override
    public void pay() {
        LOG.info("Payed by CARD");
    }
}
