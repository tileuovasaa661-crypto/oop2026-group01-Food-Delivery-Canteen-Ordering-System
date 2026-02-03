package service.delivery;

import java.math.BigDecimal;

public class DeliveryOptionImpl implements DeliveryOption {
    public String code() { return "DELIVERY"; }
    public BigDecimal fee() { return new BigDecimal("500"); }
    public boolean requiresAddress() { return true; }
}

