package team.zavod.di.service.impl;

import team.zavod.di.service.interfaces.PaymentSystem;

public class CardPaymentSystem implements PaymentSystem {
    @Override
    public void pay() {
        System.out.println("Payed by CARD");
    }
}
