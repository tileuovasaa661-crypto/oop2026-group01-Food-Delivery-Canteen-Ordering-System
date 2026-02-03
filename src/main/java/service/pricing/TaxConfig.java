package service.pricing;

import java.math.BigDecimal;

public final class TaxConfig {
    private static final TaxConfig INSTANCE = new TaxConfig();

    private BigDecimal taxRate = new BigDecimal("0.12"); // 12%

    private TaxConfig() {}

    public static TaxConfig getInstance() {
        return INSTANCE;
    }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
}
