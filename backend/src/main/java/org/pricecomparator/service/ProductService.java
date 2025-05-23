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

    public List<Map<String, Object>> getHistory(String name, String store, String brand, String category) {
        Path dir = Paths.get(dataPath);
        List<Map<String, Object>> allHistory = new ArrayList<>();
        try {
            // Stores selection
            List<String> stores = (store != null && !store.isBlank())
                    ? List.of(store)
                    : List.of("lidl", "kaufland", "profi");

            for (String s : stores) {
                // Get price files for the store
                List<String> priceFiles = Files.list(dir)
                        .map(Path::getFileName).map(Path::toString)
                        .filter(n -> n.startsWith(s + "_") && n.endsWith(".csv") && !n.contains("discount"))
                        .collect(Collectors.toList());

                Set<LocalDate> priceDates = priceFiles.stream()
                        .map(filename -> LocalDate.parse(filename.substring((s + "_").length(), filename.length() - 4)))
                        .collect(Collectors.toSet());

                // Collect all discounts for this product
                List<CsvParser.DiscountRecord> allDiscounts = Files.list(dir)
                        .map(Path::getFileName).map(Path::toString)
                        .filter(n -> n.startsWith(s + "_discount") && n.endsWith(".csv"))
                        .flatMap(filename -> {
                            try {
                                return CsvParser.parseDiscountCsv(Paths.get(dataPath, filename).toString()).stream();
                            } catch (IOException | CsvValidationException e) {
                                return new ArrayList<CsvParser.DiscountRecord>().stream();
                            }
                        })
                        .filter(r -> r.getName().equalsIgnoreCase(name))
                        .filter(r -> brand == null || brand.isBlank() || (r.getBrand() != null && r.getBrand().equalsIgnoreCase(brand)))
                        .filter(r -> category == null || category.isBlank() || (r.getCategory() != null && r.getCategory().equalsIgnoreCase(category)))
                        .collect(Collectors.toList());

                // All price/discount change dates
                Set<LocalDate> discountChangeDates = new HashSet<>();
                for (CsvParser.DiscountRecord d : allDiscounts) {
                    discountChangeDates.add(d.getFrom());
                    if (d.getTo() != null) discountChangeDates.add(d.getTo().plusDays(1));
                }
                Set<LocalDate> allChangeDates = new HashSet<>(priceDates);
                allChangeDates.addAll(discountChangeDates);
                List<LocalDate> sortedDates = allChangeDates.stream().sorted().collect(Collectors.toList());

                List<Map<String, Object>> history = new ArrayList<>();
                for (LocalDate date : sortedDates) {
                    Optional<LocalDate> priceDateOpt = priceDates.stream()
                            .filter(d -> !d.isAfter(date))
                            .max(Comparator.naturalOrder());
                    double basePrice = Double.NaN;
                    String foundBrand = null;
                    String foundCategory = null;

                    if (priceDateOpt.isPresent()) {
                        String priceFile = Paths.get(dataPath, s + "_" + priceDateOpt.get() + ".csv").toString();
                        List<Product> plist = CsvParser.parsePriceCsv(priceFile, s, priceDateOpt.get());
                        Optional<Product> prodOpt = plist.stream()
                                .filter(p -> p.getName().equalsIgnoreCase(name))
                                .filter(p -> brand == null || brand.isBlank() || (p.getBrand() != null && p.getBrand().equalsIgnoreCase(brand)))
                                .filter(p -> category == null || category.isBlank() || (p.getCategory() != null && p.getCategory().equalsIgnoreCase(category)))
                                .findFirst();
                        if (prodOpt.isPresent()) {
                            Product prod = prodOpt.get();
                            basePrice = prod.getPrice();
                            foundBrand = prod.getBrand();
                            foundCategory = prod.getCategory();
                        }
                    }

                    double discountPct = allDiscounts.stream()
                            .filter(r -> !date.isBefore(r.getFrom()) && !date.isAfter(r.getTo()))
                            .mapToDouble(CsvParser.DiscountRecord::getPercentage)
                            .max()
                            .orElse(0.0);

                    double discountedPrice = basePrice;
                    if (!Double.isNaN(basePrice) && discountPct > 0.0) {
                        discountedPrice = basePrice * (1.0 - discountPct / 100.0);
                    }

                    boolean addPoint = history.isEmpty();
                    if (!history.isEmpty()) {
                        Map<String, Object> prev = history.get(history.size() - 1);
                        double prevPrice = (double) prev.getOrDefault("price", Double.NaN);
                        double prevOrig = (double) prev.getOrDefault("originalPrice", Double.NaN);
                        double prevPct = (double) prev.getOrDefault("discountPct", 0.0);
                        addPoint = (Math.abs(prevPrice - discountedPrice) > 0.001)
                                || (Math.abs(prevOrig - basePrice) > 0.001)
                                || (Math.abs(prevPct - discountPct) > 0.001);
                    }

                    if (addPoint && !Double.isNaN(basePrice)) {
                        Map<String, Object> rec = new HashMap<>();
                        rec.put("date", date.toString());
                        rec.put("store", s);
                        rec.put("name", name);
                        rec.put("brand", foundBrand != null ? foundBrand : (brand != null ? brand : ""));
                        rec.put("category", foundCategory != null ? foundCategory : (category != null ? category : ""));
                        rec.put("price", discountedPrice);
                        rec.put("originalPrice", basePrice);
                        rec.put("discountPct", discountPct);
                        history.add(rec);
                    }
                }
                allHistory.addAll(history);
            }
            allHistory.sort(Comparator.comparing((Map<String, Object> m) -> (String) m.get("date"))
                    .thenComparing(m -> (String) m.get("store")));
            return allHistory;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load history", e);
        }
    }
}
