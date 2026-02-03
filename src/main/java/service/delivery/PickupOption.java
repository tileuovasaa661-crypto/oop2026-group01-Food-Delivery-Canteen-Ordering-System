package service.delivery;

import java.math.BigDecimal;

public class PickupOption implements DeliveryOption {
    public String code() { return "PICKUP"; }
    public BigDecimal fee() { return BigDecimal.ZERO; }
    public boolean requiresAddress() { return false; }
}
