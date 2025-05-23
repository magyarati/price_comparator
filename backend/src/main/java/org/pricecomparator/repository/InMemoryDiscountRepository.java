package org.pricecomparator.repository;

import com.opencsv.exceptions.CsvValidationException;
import org.pricecomparator.model.Discount;
import org.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class InMemoryDiscountRepository implements DiscountRepository {

    private final List<Discount> discounts = new ArrayList<>();

    public InMemoryDiscountRepository() {
        try {
            Path dir = Paths.get("data");
            // Find all .csv files containing 'discount' in the filename (case-insensitive)
            List<Path> files = Files.list(dir)
                    .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".csv"))
                    .filter(f -> f.getFileName().toString().toLowerCase().contains("discount"))
                    .collect(Collectors.toList());

            for (Path path : files) {
                String filename = path.getFileName().toString();
                // Example filename: kaufland_discounts_2025-05-08.csv
                // Extract store name up to first underscore (for compatibility with your naming)
                int sep = filename.indexOf('_');
                if (sep == -1) continue;
                String store = capitalize(filename.substring(0, sep));
                loadDiscountsFromCsv(path.toString(), store);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadDiscountsFromCsv(String filePath, String store) {
        try {
            List<CsvParser.DiscountRecord> parsed = CsvParser.parseDiscountCsv(filePath);
            for (CsvParser.DiscountRecord rec : parsed) {
                discounts.add(new Discount(
                        store,
                        rec.getName(),
                        -1, // Placeholder for discountedPrice (set later in service)
                        rec.getFrom(),
                        rec.getTo(),
                        rec.getPercentage()
                ));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    private String capitalize(String s) {
        // Makes "kaufland" -> "Kaufland" (optional, for nicer store names)
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discounts;
    }
}
