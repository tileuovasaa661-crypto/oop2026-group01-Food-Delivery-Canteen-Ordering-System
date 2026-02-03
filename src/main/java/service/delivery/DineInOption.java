package service.delivery;

import java.math.BigDecimal;

public class DineInOption implements DeliveryOption {
    public String code() { return "DINE_IN"; }
    public BigDecimal fee() { return BigDecimal.ZERO; }
    public boolean requiresAddress() { return false; }
}
