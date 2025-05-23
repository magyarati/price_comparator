package org.pricecomparator.dto;

import org.pricecomparator.model.Discount;
import org.pricecomparator.model.Product;

public class BestDiscountDto {
    private String store;
    private String productName;
    private String productCategory;
    private String brand;
    private double originalPrice;
    private double discountedPrice;
    private double discountPercent;

    public BestDiscountDto(String store, String productName, String productCategory,
                           String brand, double originalPrice, double discountedPrice, double discountPercent) {
        this.store = store;
        this.productName = productName;
        this.productCategory = productCategory;
        this.brand = brand;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.discountPercent = discountPercent;
    }

    public static BestDiscountDto fromDiscountAndProduct(Discount discount, Product product) {
        String category = (product != null) ? product.getCategory() : null;
        String brand = (product != null) ? product.getBrand() : null;
        double originalPrice = (product != null) ? product.getPrice() : -1;
        double discountPercent = discount.getPercentage();

        // Compute discounted price if not provided or invalid
        double discountedPrice = discount.getDiscountedPrice();
        if ((discountedPrice <= 0) && originalPrice > 0 && discountPercent > 0) {
            discountedPrice = Math.round(originalPrice * (1 - discountPercent / 100.0) * 100.0) / 100.0;
        }

        return new BestDiscountDto(
                discount.getStore(),
                discount.getProductName(),
                category,
                brand,
                originalPrice,
                discountedPrice,
                discountPercent
        );
    }

    public String getStore() { return store; }
    public String getProductName() { return productName; }
    public String getProductCategory() { return productCategory; }
    public String getBrand() { return brand; }
    public double getOriginalPrice() { return originalPrice; }
    public double getDiscountedPrice() { return discountedPrice; }
    public double getDiscountPercent() { return discountPercent; }
}
