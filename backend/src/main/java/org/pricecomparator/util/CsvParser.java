package org.pricecomparator.util;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import org.pricecomparator.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class CsvParser {
    private static final Logger logger = LoggerFactory.getLogger(CsvParser.class);

    public static class DiscountRecord {

        private final String id;
        private final String name;
        private final String brand;
        private final double quantity;
        private final String unit;
        private final String category;
        private final LocalDate from;
        private final LocalDate to;
        private final double percentage;

        public DiscountRecord(String id, String name, String brand, double quantity, String unit, String category,
                              LocalDate from, LocalDate to, double percentage) {
            this.id = id;
            this.name = name;
            this.brand = brand;
            this.quantity = quantity;
            this.unit = unit;
            this.category = category;
            this.from = from;
            this.to = to;
            this.percentage = percentage;
        }

        // Getters for all fields...
        public String getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getBrand() { return brand; }
        public double getQuantity() { return quantity; }
        public String getUnit() { return unit; }
        public LocalDate getFrom() { return from; }
        public LocalDate getTo() { return to; }
        public double getPercentage() { return percentage; }
    }

    public static List<Product> parsePriceCsv(String path, String store)
            throws IOException, CsvValidationException {
        List<Product> list = new ArrayList<>();
        try (CSVReader r = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            String[] row;
            // header
            if ((row = r.readNext()) != null && !isDataRow(row)) {
                logger.info("Skipping header: {}", Arrays.toString(row));
            } else if (row != null) {
                parsePriceRow(row, store, list);
            }
            while ((row = r.readNext()) != null) {
                if (!isDataRow(row)) {
                    logger.warn("Skipping malformed price row: {}", Arrays.toString(row));
                    continue;
                }
                parsePriceRow(row, store, list);
            }
        }
        return list;
    }

    public static List<DiscountRecord> parseDiscountCsv(String path)
            throws IOException, CsvValidationException {
        List<DiscountRecord> list = new ArrayList<>();
        try (CSVReader r = new CSVReaderBuilder(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))
                .withCSVParser(new CSVParserBuilder().withSeparator(';').build())
                .build()) {
            r.readNext(); // skip header
            String[] row;
            while ((row = r.readNext()) != null) {
                if (row.length != 9) {
                    logger.warn("Skipping malformed discount row: {}", Arrays.toString(row));
                    continue;
                }
                try {
                    DiscountRecord discount = new DiscountRecord(
                            row[0], // id
                            row[1], // name
                            row[2], // brand
                            Double.parseDouble(row[3]), // quantity
                            row[4], // unit
                            row[5], // category
                            LocalDate.parse(row[6]), // from
                            LocalDate.parse(row[7]), // to
                            Double.parseDouble(row[8]) // percentage
                    );
                    list.add(discount);
                } catch (Exception e) {
                    logger.warn("Error parsing discount row {}: {}", Arrays.toString(row), e.getMessage());
                }
            }
        }
        return list;
    }


    private static boolean isDataRow(String[] row) {
        // expect 8 cols: id;name;category;brand;qty;unit;price;currency
        return row.length == 8 && isDouble(row[4]) && isDouble(row[6]);
    }

    private static void parsePriceRow(String[] row, String store, List<Product> list) {
        try {
            Product p = new Product();
            p.setStore(store);
            p.setName(row[1]);
            p.setCategory(row[2]);
            p.setBrand(row[3]);
            p.setGrammage(Double.parseDouble(row[4]));
            p.setUnit(row[5]);
            p.setPrice(Double.parseDouble(row[6]));
            p.setCurrency(row[7]);
            list.add(p);
        } catch (NumberFormatException e) {
            logger.warn("Number format error parsing price row {}: {}", Arrays.toString(row), e.getMessage());
        }
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
