package org.pricecomparator.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BasketResponse {
    private double totalCost;
    private Map<String, List<ItemSummary>> storeLists;
    private LocalDate date; // Optional
    private String winningStore; // <-- Added field

    public static class ItemSummary {
        private String name;
        private double quantity;
        private double packageQuantity;
        private String packageUnit;
        private double packagePrice;
        private double unitPrice;
        private String unit;
        private double cost;
        private String currency;
        private String category;
        private String brand;
        private boolean bestUnitPrice;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }

        public double getPackageQuantity() { return packageQuantity; }
        public void setPackageQuantity(double packageQuantity) { this.packageQuantity = packageQuantity; }

        public String getPackageUnit() { return packageUnit; }
        public void setPackageUnit(String packageUnit) { this.packageUnit = packageUnit; }

        public double getPackagePrice() { return packagePrice; }
        public void setPackagePrice(double packagePrice) { this.packagePrice = packagePrice; }

        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }

        public boolean isBestUnitPrice() { return bestUnitPrice; }
        public void setBestUnitPrice(boolean bestUnitPrice) { this.bestUnitPrice = bestUnitPrice; }
    }

    // --- Getters and Setters for BasketResponse ---
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public Map<String, List<ItemSummary>> getStoreLists() { return storeLists; }
    public void setStoreLists(Map<String, List<ItemSummary>> storeLists) { this.storeLists = storeLists; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getWinningStore() { return winningStore; }      // <-- Added getter
    public void setWinningStore(String winningStore) { this.winningStore = winningStore; } // <-- Added setter
}
