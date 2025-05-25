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
        loadAll();
    }

    /**
     * Reloads products and discounts from the CSV files.
     * Thread-safe for concurrent reloads.
     */
    public synchronized void reload() {
        products.clear();
        loadAll();
    }

    /**
     * Loads all products and applies discounts.
     * (You can call this in constructor and in reload())
     */
    private void loadAll() {
        Map<String, List<Product>> productsByKey = new HashMap<>();
        try {
            Path dir = Paths.get("data");
            List<Path> files = Files.list(dir)
                    .filter(f -> f.getFileName().toString().endsWith(".csv"))
                    .filter(f -> !f.getFileName().toString().toLowerCase().contains("discount"))
                    .sorted(Comparator.comparing(f -> extractDateFromFilename(f.getFileName().toString())))
                    .collect(Collectors.toList());

            for (Path path : files) {
                String filename = path.getFileName().toString();
                int sep = filename.indexOf('_');
                if (sep == -1) continue;
                String store = filename.substring(0, sep).toLowerCase();
                LocalDate priceDate = extractDateFromFilename(filename);

                List<Product> loaded = CsvParser.parsePriceCsv(path.toString(), store, priceDate);
                for (Product p : loaded) {
                    String key = p.getStore().toLowerCase() + "|" + p.getName().toLowerCase();
                    productsByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(p);
                }
            }

            // Set validUntil for each price segment
            List<Product> loadedProducts = new ArrayList<>();
            for (List<Product> group : productsByKey.values()) {
                group.sort(Comparator.comparing(Product::getValidFrom));
                for (int i = 0; i < group.size(); i++) {
                    Product p = group.get(i);
                    if (i + 1 < group.size()) {
                        p.setValidUntil(group.get(i + 1).getValidFrom().minusDays(1));
                    } else {
                        p.setValidUntil(p.getValidFrom().plusYears(1));
                    }
                    loadedProducts.add(p);
                }
            }

            // --- Load discounts with correct store assignment ---
            List<CsvParser.DiscountRecord> allDiscounts = new ArrayList<>();
            List<Path> discountFiles = Files.list(dir)
                    .filter(f -> f.getFileName().toString().toLowerCase().contains("discount"))
                    .filter(f -> f.getFileName().toString().endsWith(".csv"))
                    .collect(Collectors.toList());
            for (Path dfile : discountFiles) {
                String fname = dfile.getFileName().toString();
                int sep = fname.indexOf('_');
                String store = (sep == -1) ? "" : fname.substring(0, sep).toLowerCase();
                allDiscounts.addAll(CsvParser.parseDiscountCsv(dfile.toString(), store));
            }

            // --- Apply discount to each product ---
            for (Product p : loadedProducts) {
                Optional<CsvParser.DiscountRecord> discountOpt = allDiscounts.stream()
                        .filter(d -> d.getName().equalsIgnoreCase(p.getName()))
                        .filter(d -> d.getStore() == null || d.getStore().equalsIgnoreCase(p.getStore()))
                        // If you want to match by brand, category, or unit, add more filters here
                        .filter(d -> {
                            LocalDate pFrom = p.getValidFrom(), pTo = p.getValidUntil();
                            LocalDate dFrom = d.getFrom(), dTo = d.getTo();
                            return !pTo.isBefore(dFrom) && !pFrom.isAfter(dTo);
                        })
                        .max(Comparator.comparing(CsvParser.DiscountRecord::getPercentage));

                if (discountOpt.isPresent()) {
                    p.setDiscount(discountOpt.get().getPercentage());
                } else {
                    p.setDiscount(0.0);
                }
            }

            products.addAll(loadedProducts);

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private static LocalDate extractDateFromFilename(String filename) {
        int sep = filename.indexOf('_');
        int dot = filename.lastIndexOf('.');
        if (sep != -1 && dot != -1 && sep + 1 < dot) {
            String dateStr = filename.substring(sep + 1, dot);
            return LocalDate.parse(dateStr);
        }
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
        return new ArrayList<>(products);
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
