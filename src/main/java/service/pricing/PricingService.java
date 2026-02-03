package service.pricing;

import service.delivery.DeliveryOption;

import java.math.BigDecimal;

public class PricingService {

    public BigDecimal applyFeesAndTax(BigDecimal itemsTotal, DeliveryOption option) {
        BigDecimal subtotal = itemsTotal.add(option.fee());
        BigDecimal tax = subtotal.multiply(TaxConfig.getInstance().getTaxRate());
        return subtotal.add(tax);
    }
}

