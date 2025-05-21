package org.example.pricecomparator.repository;

import org.example.pricecomparator.model.Product;
import org.example.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public InMemoryProductRepository() {
        try {
            products.addAll(CsvParser.parsePriceCsv("data/kaufland_2025-05-08.csv", "Kaufland"));
            products.addAll(CsvParser.parsePriceCsv("data/lidl_2025-05-08.csv", "Lidl"));
            products.addAll(CsvParser.parsePriceCsv("data/profi_2025-05-08.csv", "Profi"));
            // Add other stores/files as needed
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> getAllProducts() {
        return products;
    }

    @Override
    public Optional<Product> findByStoreAndProductName(String store, String productName) {
        return products.stream()
                .filter(p -> p.getStore().equalsIgnoreCase(store)
                        && p.getName().equalsIgnoreCase(productName))
                .findFirst();
    }

    @Override
    public List<Product> getPriceHistory(String productId) {
        // TODO: Adapt field if you have a real productId; for now, match on name
        return products.stream()
                .filter(p -> productId.equals(p.getName()))
                .collect(Collectors.toList());
    }


}
