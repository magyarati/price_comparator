package org.pricecomparator.dto;
import java.time.LocalDate;
import java.util.List;

/**
 * Used for both single-store and split-basket optimizations.
 * Contains the shopping list and the date for price selection.
 */
public class BasketRequest {

    private List<Item> items;
    private LocalDate date;

    public static class Item {
        private String productName;
        private double quantity;

        // getters and setters...
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public double getQuantity() { return quantity; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
    }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
