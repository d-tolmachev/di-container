package team.zavod.di.service.impl;

import team.zavod.di.annotation.Component;
import team.zavod.di.service.interfaces.PaymentSystem;

@Component
public class CardPaymentSystem implements PaymentSystem {
    @Override
    public void pay() {
        System.out.println("Payed by CARD");
    }
}
