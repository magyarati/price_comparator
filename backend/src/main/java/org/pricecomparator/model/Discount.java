package org.pricecomparator.model;

import java.time.LocalDate;

public class Discount {
    private String store;
    private String productName;
    private double discountedPrice; // can be -1 if not supplied
    private double percentage;
    private LocalDate validFrom;
    private LocalDate validUntil;

    public Discount(String store, String productName, double discountedPrice,
                    LocalDate validFrom, LocalDate validUntil, double percentage) {
        this.store = store;
        this.productName = productName;
        this.discountedPrice = discountedPrice;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.percentage = percentage;
    }

    public String getStore() { return store; }
    public String getProductName() { return productName; }
    public double getDiscountedPrice() { return discountedPrice; }
    public double getPercentage() { return percentage; }
    public LocalDate getValidFrom() { return validFrom; }
    public LocalDate getValidUntil() { return validUntil; }
}
