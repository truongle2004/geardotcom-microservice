package web_ecommerce.core.enums;

public enum NovuTriggerEvent {
    PAYMENT_SUCCESS("payment-success");

    private String event;

    NovuTriggerEvent(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }
}
