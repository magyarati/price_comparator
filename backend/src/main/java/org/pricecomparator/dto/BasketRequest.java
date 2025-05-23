package org.pricecomparator.dto;

import java.util.List;

public class BasketRequest {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        private String productName;
        private double quantity;

        public String getProductName() {
            return productName;
        }
        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getQuantity() {
            return quantity;
        }
        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }
    }
}
