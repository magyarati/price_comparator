package org.pricecomparator.dto;

import java.util.List;
import java.util.Map;

public class BasketResponse {
    public double totalCost;
    public Map<String, List<OptimizedItem>> storeLists;

    public static class OptimizedItem {
        public String name;
        public double quantity;
        public double unitPrice;
        public double cost;
    }
}
