package org.pricecomparator.repository;

import org.pricecomparator.model.Product;
import org.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public InMemoryProductRepository() {
        // Map of key "store|name" -> list of Product price segments
        Map<String, List<Product>> productsByKey = new HashMap<>();
        try {
            Path dir = Paths.get("data");
            // Only .csv files not containing 'discount'
            List<Path> files = Files.list(dir)
                    .filter(f -> f.getFileName().toString().endsWith(".csv"))
                    .filter(f -> !f.getFileName().toString().toLowerCase().contains("discount"))
                    .sorted(Comparator.comparing(f -> extractDateFromFilename(f.getFileName().toString())))
                    .collect(Collectors.toList());

            for (Path path : files) {
                String filename = path.getFileName().toString();
                int sep = filename.indexOf('_');
                if (sep == -1) continue;
                String store = filename.substring(0, sep);
                LocalDate priceDate = extractDateFromFilename(filename);

                List<Product> loaded = CsvParser.parsePriceCsv(path.toString(), store, priceDate);
                for (Product p : loaded) {
                    String key = p.getStore().toLowerCase() + "|" + p.getName().toLowerCase();
                    productsByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
                }
            }
            // Now set validUntil for each product price segment
            for (List<Product> group : productsByKey.values()) {
                group.sort(Comparator.comparing(Product::getValidFrom));
                for (int i = 0; i < group.size(); i++) {
                    Product p = group.get(i);
                    if (i + 1 < group.size()) {
                        // valid until day before next segment
                        p.setValidUntil(group.get(i + 1).getValidFrom().minusDays(1));
                    } else {
                        // valid for 1 year if no newer price
                        p.setValidUntil(p.getValidFrom().plusYears(1));
                    }
                    products.add(p);
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    // Helper to extract date from filename like "kaufland_2025-05-01.csv"
    private static LocalDate extractDateFromFilename(String filename) {
        int sep = filename.indexOf('_');
        int dot = filename.lastIndexOf('.');
        if (sep != -1 && dot != -1 && sep + 1 < dot) {
            String dateStr = filename.substring(sep + 1, dot);
            return LocalDate.parse(dateStr);
        }
        // fallback
        return LocalDate.now();
    }

    @Override
    public Set<String> getAllStores() {
        return products.stream()
                .map(Product::getStore)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<Product> findByNameAndStoreAndDate(String name, String store, LocalDate date) {
        return products.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .filter(p -> p.getStore().equalsIgnoreCase(store))
                .filter(p -> p.getValidFrom() != null && p.getValidUntil() != null)
                .filter(p -> !date.isBefore(p.getValidFrom()) && !date.isAfter(p.getValidUntil()))
                .findFirst();
    }

    @Override
    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Defensive copy
    }

    @Override
    public Optional<Product> findByStoreAndProductName(String store, String productName) {
        return products.stream()
                .filter(p -> p.getStore().equalsIgnoreCase(store))
                .filter(p -> p.getName().equalsIgnoreCase(productName))
                .max(Comparator.comparing(Product::getValidFrom));
    }

    @Override
    public List<Product> getPriceHistory(String productId) {
        return products.stream()
                .filter(p -> productId.equals(p.getName()))
                .sorted(Comparator.comparing(Product::getValidFrom))
                .collect(Collectors.toList());
    }

    public void addProduct(Product product) {
        products.add(product);
    }
}
