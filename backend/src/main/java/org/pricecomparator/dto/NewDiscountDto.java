package org.pricecomparator.dto;

import org.pricecomparator.model.Discount;
import org.pricecomparator.model.Product;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a newly added discount,
 * including relevant product details for display and API purposes.
 */
public class NewDiscountDto {
    private String store;
    private String productName;
    private String productCategory;
    private String brand;
    private double quantity;          // maps to Product.grammage
    private String unit;              // maps to Product.unit
    private double originalPrice;
    private double discountedPrice;
    private double discountPercent;
    private LocalDate validFrom;
    private LocalDate validUntil;

    // Default constructor
    public NewDiscountDto() {}

    // Full constructor
    public NewDiscountDto(String store, String productName, String productCategory,
                          String brand, double quantity, String unit,
                          double originalPrice, double discountedPrice,
                          double discountPercent, LocalDate validFrom, LocalDate validUntil) {
        this.store = store;
        this.productName = productName;
        this.productCategory = productCategory;
        this.brand = brand;
        this.quantity = quantity;
        this.unit = unit;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    /**
     * Builds a NewDiscountDto from Discount and (optional) Product.
     * Handles missing product info and computes discounted price if needed.
     * Null product fields become empty string for frontend safety.
     */
    public static NewDiscountDto fromDiscountAndProduct(Discount discount, Product product) {
        String productCategory = product != null ? safe(product.getCategory()) : "";
        String brand = product != null ? safe(product.getBrand()) : "";
        double originalPrice = product != null ? product.getPrice() : 0;
        double discountPercent = discount.getPercentage();
        double discountedPrice = discount.getDiscountedPrice();

        // Use grammage and unit from Product as quantity/unit
        double quantity = product != null ? product.getGrammage() : 0;
        String unit = product != null ? safe(product.getUnit()) : "";

        // Compute discountedPrice if missing
        if ((discountedPrice <= 0) && originalPrice > 0 && discountPercent > 0) {
            discountedPrice = Math.round(originalPrice * (1 - discountPercent / 100.0) * 100.0) / 100.0;
        }

        return new NewDiscountDto(
                safe(discount.getStore()),
                safe(discount.getProductName()),
                productCategory,
                brand,
                quantity,
                unit,
                originalPrice,
                discountedPrice,
                discountPercent,
                discount.getValidFrom(),
                discount.getValidUntil()
        );
    }

    // Utility to replace null strings with ""
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    // --- Getters and Setters (unchanged) ---

    public String getStore() {
        return store;
    }
    public void setStore(String store) {
        this.store = store;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductCategory() {
        return productCategory;
    }
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public double getOriginalPrice() {
        return originalPrice;
    }
    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }
    public double getDiscountedPrice() {
        return discountedPrice;
    }
    public void setDiscountedPrice(double discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
    public double getDiscountPercent() {
        return discountPercent;
    }
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }
    public LocalDate getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }
    public LocalDate getValidUntil() {
        return validUntil;
    }
    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }
}
