// src/main/java/org/pricecomparator/model/PriceAlert.java
package org.pricecomparator.model;

public class PriceAlert {
    private Long id;
    private String email;
    private String productName;
    private String store; // Optional: null/empty = any store
    private Double targetPrice;

    public PriceAlert() {}

    public PriceAlert(String email, String productName, String store, Double targetPrice) {
        this.email = email;
        this.productName = productName;
        this.store = store;
        this.targetPrice = targetPrice;
    }

    // Getters and Setters...
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }
    public Double getTargetPrice() { return targetPrice; }
    public void setTargetPrice(Double targetPrice) { this.targetPrice = targetPrice; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
