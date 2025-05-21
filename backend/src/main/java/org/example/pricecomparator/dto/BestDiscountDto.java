package org.example.pricecomparator.dto;

public class BestDiscountDto {
    private String store;
    private String productName;
    private String productCategory;
    private String brand;
    private double originalPrice;
    private double discountedPrice;
    private double discountPercent;

    public BestDiscountDto(String store, String productName, String productCategory, String brand,
                           double originalPrice, double discountedPrice, double discountPercent) {
        this.store = store;
        this.productName = productName;
        this.productCategory = productCategory;
        this.brand = brand;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
    }

    // Getters and setters

    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductCategory() { return productCategory; }
    public void setProductCategory(String productCategory) { this.productCategory = productCategory; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(double discountedPrice) { this.discountedPrice = discountedPrice; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
}
