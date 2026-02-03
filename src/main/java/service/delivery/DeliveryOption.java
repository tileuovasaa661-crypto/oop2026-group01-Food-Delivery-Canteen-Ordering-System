package service.delivery;

import java.math.BigDecimal;

public interface DeliveryOption {
    String code();
    BigDecimal fee();
    boolean requiresAddress();
}
