package org.pricecomparator.service;

import org.pricecomparator.model.Product;
import org.pricecomparator.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Value("${data.csv.path:data}")
    private String dataPath;

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll(String date, String store) {
        List<String> stores = (store != null && !store.isBlank()) ? List.of(store) : null;
        // dateStr is used, startDate/endDate/brands/names left null for compatibility
        return getAllFiltered(
                stores,
                null, // names
                null, // brands
                date, // dateStr
                null, // startDateStr
                null  // endDateStr
        );
    }

    /**
     * Filters products using the in-memory repository.
     * Supports multi-value store/name/brand/date/date range filters.
     */
    public List<Product> getAllFiltered(
            List<String> stores, List<String> names, List<String> brands,
            String dateStr, String startDateStr, String endDateStr
    ) {
        List<Product> products = productRepository.getAllProducts();

        // Parse date filters
        LocalDate filterDate = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : null;
        LocalDate startDate = (startDateStr != null && !startDateStr.isBlank()) ? LocalDate.parse(startDateStr) : null;
        LocalDate endDate = (endDateStr != null && !endDateStr.isBlank()) ? LocalDate.parse(endDateStr) : null;

        return products.stream()
                .filter(p -> stores == null || stores.isEmpty() || stores.stream().anyMatch(s -> s.equalsIgnoreCase(p.getStore())))
                .filter(p -> names == null || names.isEmpty() || names.stream().anyMatch(n -> n.equalsIgnoreCase(p.getName())))
                .filter(p -> brands == null || brands.isEmpty() || brands.stream().anyMatch(b -> b.equalsIgnoreCase(p.getBrand())))
                .filter(p -> {
                    if (filterDate != null) {
                        // Product must be valid for this date
                        return !filterDate.isBefore(p.getValidFrom()) && !filterDate.isAfter(p.getValidUntil());
                    }
                    if (startDate != null && endDate != null) {
                        // Product must be valid at least for one day in the interval
                        return !(p.getValidUntil().isBefore(startDate) || p.getValidFrom().isAfter(endDate));
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Returns price/discount history for a product using only the in-memory repository.
     */
    public List<Map<String, Object>> getHistory(String name, String store, String brand, String category) {
        List<Product> allProducts = productRepository.getAllProducts();

        // Filter by parameters
        return allProducts.stream()
                .filter(p -> p.getName().equalsIgnoreCase(name))
                .filter(p -> store == null || store.isBlank() || p.getStore().equalsIgnoreCase(store))
                .filter(p -> brand == null || brand.isBlank() || (p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand)))
                .filter(p -> category == null || category.isBlank() || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)))
                .sorted(Comparator.comparing(Product::getValidFrom))
                .map(p -> {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("date", p.getValidFrom().toString());
                    rec.put("store", p.getStore());
                    rec.put("name", p.getName());
                    rec.put("brand", p.getBrand() != null ? p.getBrand() : "");
                    rec.put("category", p.getCategory() != null ? p.getCategory() : "");
                    rec.put("price", p.getPrice() * (1.0 - p.getDiscount() / 100.0));
                    rec.put("originalPrice", p.getPrice());
                    rec.put("discountPct", p.getDiscount());
                    return rec;
                })
                .collect(Collectors.toList());
    }
}
