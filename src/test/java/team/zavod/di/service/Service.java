package team.zavod.di.service;

import team.zavod.di.annotation.Autowired;
import team.zavod.di.annotation.Component;
import team.zavod.di.service.interfaces.PaymentSystem;

@Component
public class Service {
    private final PaymentSystem paymentSystem;

        @Autowired
        public Service(PaymentSystem paymentSystem) {
        this.paymentSystem = paymentSystem;
        }

    public void run() {
        this.paymentSystem.pay();
    }
}
