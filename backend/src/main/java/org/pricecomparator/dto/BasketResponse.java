package org.pricecomparator.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class BasketResponse {
    private double totalCost;
    private Map<String, List<ItemSummary>> storeLists;
    private LocalDate date; // Optional

    public static class ItemSummary {
        private String name;
        private double quantity;
        private double unitPrice;
        private double cost;

        // getters and setters...
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
        public double getCost() { return cost; }
        public void setCost(double cost) { this.cost = cost; }
    }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public Map<String, List<ItemSummary>> getStoreLists() { return storeLists; }
    public void setStoreLists(Map<String, List<ItemSummary>> storeLists) { this.storeLists = storeLists; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
