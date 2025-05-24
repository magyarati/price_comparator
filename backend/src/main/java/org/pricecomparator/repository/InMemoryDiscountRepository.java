package org.pricecomparator.repository;

import com.opencsv.exceptions.CsvValidationException;
import org.pricecomparator.model.Discount;
import org.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryDiscountRepository implements DiscountRepository {

    private final List<Discount> discounts = new ArrayList<>();

    public InMemoryDiscountRepository() {
        loadAll();
    }

    /**
     * Reloads all discount data from CSV files at runtime.
     */
    public synchronized void reload() {
        discounts.clear();
        loadAll();
    }

    /**
     * Loads all discounts from disk into the in-memory list.
     */
    private void loadAll() {
        try {
            Path dir = Paths.get("data");
            List<Path> files = Files.list(dir)
                    .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".csv"))
                    .filter(f -> f.getFileName().toString().toLowerCase().contains("discount"))
                    .collect(Collectors.toList());

            List<Discount> loadedDiscounts = new ArrayList<>();

            for (Path path : files) {
                String filename = path.getFileName().toString();
                int sep = filename.indexOf('_');
                if (sep == -1) continue;
                String store = filename.substring(0, sep).toLowerCase();
                List<CsvParser.DiscountRecord> parsed = CsvParser.parseDiscountCsv(path.toString(), store);
                for (CsvParser.DiscountRecord rec : parsed) {
                    loadedDiscounts.add(new Discount(
                            store,
                            rec.getName(),
                            -1, // Placeholder for discountedPrice (set later in service)
                            rec.getFrom(),
                            rec.getTo(),
                            rec.getPercentage()
                    ));
                }
            }
            discounts.addAll(loadedDiscounts);
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts); // Defensive copy
    }

    @Override
    public Optional<Discount> findBestDiscount(String productName, String store, LocalDate date) {
        return discounts.stream()
                .filter(d -> d.getProductName().equalsIgnoreCase(productName))
                .filter(d -> d.getStore().equalsIgnoreCase(store))
                .filter(d -> d.getValidFrom() != null && d.getValidUntil() != null)
                .filter(d -> !date.isBefore(d.getValidFrom()) && !date.isAfter(d.getValidUntil()))
                .max(Comparator.comparing(Discount::getPercentage));
    }

    public List<Discount> getNewDiscountsWithin24Hours() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusHours(24);
        return discounts.stream()
                .filter(d -> d.getValidFrom().isAfter(ChronoLocalDate.from(yesterday)))
                .collect(Collectors.toList());
    }
}
