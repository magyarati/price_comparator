package org.example.pricecomparator.model;

import java.time.LocalDate;

public class Discount {
    private String store;
    private String productName;
    private double discountedPrice;
    private LocalDate from;
    private LocalDate to;
    private double percentage;

    public Discount(String store, String productName, double discountedPrice, LocalDate from, LocalDate to, double percentage) {
        this.store = store;
        this.productName = productName;
        this.discountedPrice = discountedPrice;
        this.from = from;
        this.to = to;
        this.percentage = percentage;
    }

    public String getStore() { return store; }
    public String getProductName() { return productName; }
    public double getDiscountedPrice() { return discountedPrice; }
    public LocalDate getFrom() { return from; }
    public LocalDate getTo() { return to; }
    public double getPercentage() { return percentage; }
}
