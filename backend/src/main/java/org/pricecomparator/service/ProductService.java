package org.pricecomparator.service;

import org.pricecomparator.model.Product;
import org.pricecomparator.util.CsvParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Value("${data.csv.path:data}")
    private String dataPath;

    public List<Product> getAll(String date, String store) throws IOException, CsvValidationException {
        LocalDate requested = (date != null) ? LocalDate.parse(date) : LocalDate.now();
        List<Product> result = new ArrayList<>();

        for (String s : List.of("lidl", "kaufland", "profi")) {
            if (store != null && !s.equalsIgnoreCase(store)) continue;

            Path dir = Paths.get(dataPath);
            List<LocalDate> dates = Files.list(dir)
                    .map(Path::getFileName).map(Path::toString)
                    .filter(n -> n.startsWith(s + "_") && n.endsWith(".csv") && !n.contains("discount"))
                    .map(n -> LocalDate.parse(n.substring((s + "_").length(), n.length() - 4)))
                    .sorted().collect(Collectors.toList());
            if (dates.isEmpty()) continue;

            // Find all price periods up to the requested date
            for (int i = 0; i < dates.size(); i++) {
                LocalDate useDate = dates.get(i);
                if (useDate.isAfter(requested)) break; // only load up to requested date

                LocalDate next = (i + 1 < dates.size()) ? dates.get(i + 1) : useDate.plusYears(1);
                LocalDate validUntil = next.minusDays(1);

                String priceFile = Paths.get(dataPath, s + "_" + useDate + ".csv").toString();
                List<Product> prices = CsvParser.parsePriceCsv(priceFile, s, useDate);

                List<CsvParser.DiscountRecord> discounts = new ArrayList<>();
                Files.list(dir)
                        .map(Path::getFileName).map(Path::toString)
                        .filter(name -> name.startsWith(s + "_discounts_") && name.endsWith(".csv"))
                        .forEach(name -> {
                            try {
                                discounts.addAll(CsvParser.parseDiscountCsv(Paths.get(dataPath, name).toString()));
                            } catch (IOException | CsvValidationException e) {
                                // log or handle appropriately
                            }
                        });

                for (Product p : prices) {
                    double pct = discounts.stream()
                            .filter(r -> r.getName().equalsIgnoreCase(p.getName()))
                            .filter(r -> !requested.isBefore(r.getFrom()) && !requested.isAfter(r.getTo()))
                            .mapToDouble(CsvParser.DiscountRecord::getPercentage).findFirst().orElse(0.0);
                    p.setDiscount(pct);
                    p.setValidFrom(useDate);
                    p.setValidUntil(validUntil);
                }

                // Only add products valid for the requested date
                for (Product p : prices) {
                    if (!requested.isBefore(p.getValidFrom()) && !requested.isAfter(p.getValidUntil())) {
                        result.add(p);
                    }
                }
            }
        }
        return result;
    }

    public List<Map<String, Object>> getHistory(String store, String name) {
        Path dir = Paths.get(dataPath);
        try {
            return Files.list(dir)
                    .map(Path::getFileName).map(Path::toString)
                    .filter(n -> n.startsWith(store + "_") && n.endsWith(".csv") && !n.contains("discount"))
                    .map(filename -> {
                        LocalDate date = LocalDate.parse(filename.substring((store + "_").length(), filename.length() - 4));
                        List<Product> list;
                        try {
                            list = CsvParser.parsePriceCsv(Paths.get(dataPath, filename).toString(), store, date);
                        } catch (IOException | CsvValidationException e) {
                            throw new RuntimeException("Failed to parse CSV file: " + filename, e);
                        }
                        double price = list.stream()
                                .filter(p -> p.getName().equalsIgnoreCase(name))
                                .map(Product::getPrice).findFirst().orElse(Double.NaN);
                        Map<String, Object> rec = new HashMap<>();
                        rec.put("date", date.toString());
                        rec.put("price", price);
                        return rec;
                    })
                    .sorted(Comparator.comparing(m -> LocalDate.parse((String) m.get("date"))))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load history", e);
        }
    }
}
