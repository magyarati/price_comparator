package org.example.pricecomparator.repository;

import com.opencsv.exceptions.CsvValidationException;
import org.example.pricecomparator.model.Discount;
import org.example.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryDiscountRepository implements DiscountRepository {

    private final List<Discount> discounts = new ArrayList<>();

    public InMemoryDiscountRepository() {
        // Example loading from actual files and adding store info:
        loadDiscountsFromCsv("data/kaufland_discounts_2025-05-08.csv", "Kaufland");
        loadDiscountsFromCsv("data/lidl_discounts_2025-05-08.csv", "Lidl");
        loadDiscountsFromCsv("data/profi_discounts_2025-05-08.csv", "Profi");
        // Add other files/stores as needed
    }

    private void loadDiscountsFromCsv(String filePath, String store) {
        try {
            List<CsvParser.DiscountRecord> parsed = CsvParser.parseDiscountCsv(filePath);
            for (CsvParser.DiscountRecord rec : parsed) {
                // Discounted price calculation: If you know the original, you can get it from Product later
                discounts.add(new Discount(
                        store,
                        rec.getName(),
                        -1, // Will be set in DiscountService; here use -1 as placeholder
                        rec.getFrom(),
                        rec.getTo(),
                        rec.getPercentage()
                ));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Discount> getAllDiscounts() {
        return discounts;
    }
}
