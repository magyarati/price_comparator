package org.example.pricecomparator.model;

public class Product {
    private String store;
    private String name;
    private String category;
    private String brand;
    private double grammage;
    private String unit;
    private double price;
    private String currency;
    private double discount = 0.0;
    private java.time.LocalDate validFrom;
    private java.time.LocalDate validUntil;

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }

    public java.time.LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(java.time.LocalDate validFrom) { this.validFrom = validFrom; }

    public java.time.LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(java.time.LocalDate validUntil) { this.validUntil = validUntil; }

    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getGrammage() { return grammage; }
    public void setGrammage(double grammage) { this.grammage = grammage; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getId() {return name;
    }
}
