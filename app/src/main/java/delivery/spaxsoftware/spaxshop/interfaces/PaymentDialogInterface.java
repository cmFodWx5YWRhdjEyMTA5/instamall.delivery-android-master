package delivery.spaxsoftware.spaxshop.interfaces;


import delivery.spaxsoftware.spaxshop.entities.delivery.Payment;

public interface PaymentDialogInterface {
    void onPaymentSelected(Payment payment);
}
