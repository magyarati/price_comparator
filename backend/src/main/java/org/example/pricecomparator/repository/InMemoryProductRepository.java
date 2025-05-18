package org.example.pricecomparator.repository;

import org.example.pricecomparator.model.PriceEntry;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<PriceEntry> priceData = new ArrayList<>();

    public InMemoryProductRepository() {
        // Example static data
        priceData.add(new PriceEntry("1", "Milk", "Dairy", "BrandA", 1, "litru", 5.99, "RON", LocalDate.parse("2025-05-01")));
        priceData.add(new PriceEntry("1", "Milk", "Dairy", "BrandA", 1, "litru", 5.49, "RON", LocalDate.parse("2025-05-08")));
        priceData.add(new PriceEntry("2", "Bread", "Bakery", "BrandB", 1, "bucata", 3.29, "RON", LocalDate.parse("2025-05-01")));
        priceData.add(new PriceEntry("2", "Bread", "Bakery", "BrandB", 1, "bucata", 3.19, "RON", LocalDate.parse("2025-05-08")));
    }

    @Override
    public List<PriceEntry> getPriceHistory(String productId) {
        List<PriceEntry> history = new ArrayList<>();
        for (PriceEntry entry : priceData) {
            if (entry.getProductId().equals(productId)) {
                history.add(entry);
            }
        }
        return history;
    }
}