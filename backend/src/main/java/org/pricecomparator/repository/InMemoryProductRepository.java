package org.pricecomparator.repository;

import org.pricecomparator.model.Product;
import org.pricecomparator.util.CsvParser;
import org.springframework.stereotype.Repository;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final List<Product> products = new ArrayList<>();

    public InMemoryProductRepository() {
        try {
            Path dir = Paths.get("data");
            // Only .csv files not containing 'discount'
            List<Path> files = Files.list(dir)
                    .filter(f -> f.getFileName().toString().endsWith(".csv"))
                    .filter(f -> !f.getFileName().toString().toLowerCase().contains("discount"))
                    .collect(Collectors.toList());

            for (Path path : files) {
                String filename = path.getFileName().toString();
                // Assume format: storename_yyyy-mm-dd.csv
                int sep = filename.indexOf('_');
                if (sep == -1) continue;
                String store = filename.substring(0, sep);
                products.addAll(CsvParser.parsePriceCsv(path.toString(), store));
            }
        } catch (IOException | CsvValidationException e) {
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
