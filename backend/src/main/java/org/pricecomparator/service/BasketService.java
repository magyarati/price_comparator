package org.pricecomparator.service;

import org.pricecomparator.model.Product;
import org.pricecomparator.model.Discount;
import org.pricecomparator.repository.InMemoryProductRepository;
import org.pricecomparator.repository.InMemoryDiscountRepository;
import org.pricecomparator.dto.BasketRequest;
import org.pricecomparator.dto.BasketResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class BasketService {

    private final InMemoryProductRepository productRepository;
    private final InMemoryDiscountRepository discountRepository;

    public BasketService(InMemoryProductRepository productRepository, InMemoryDiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public BasketResponse optimizeBasket(List<BasketRequest.Item> items, LocalDate date, boolean allStores) {
        if (date == null) {
            date = LocalDate.now();
        }
        System.out.println("=== Basket Optimization Debug ===");
        System.out.println("Optimizing basket for date: " + date);
        System.out.println("Input items: " + items.size());
        for (BasketRequest.Item item : items) {
            System.out.println("- " + item.getProductName() + ", qty=" + item.getQuantity());
        }

        Map<String, List<BasketResponse.ItemSummary>> storeLists = new HashMap<>();

        Set<String> allStoresSet = productRepository.getAllStores();
        System.out.println("All stores in repository: " + allStoresSet);

        for (String store : allStoresSet) {
            double total = 0;
            List<BasketResponse.ItemSummary> summaries = new ArrayList<>();
            boolean allAvailable = true;
            System.out.println("\nStore: " + store);

            for (BasketRequest.Item reqItem : items) {
                Optional<Product> prodOpt = productRepository.findByNameAndStoreAndDate(reqItem.getProductName(), store, date);
                if (prodOpt.isEmpty()) {
                    System.out.println("  Product NOT found: " + reqItem.getProductName() + " in " + store + " for date " + date);
                    allAvailable = false;
                    break;
                }
                Product prod = prodOpt.get();
                System.out.println("  Product FOUND: " + prod.getName() + " (" + prod.getStore() + "), price=" + prod.getPrice());

                double price = prod.getPrice();

                Optional<Discount> discountOpt = discountRepository.findBestDiscount(prod.getName(), store, date);
                double finalPrice = price;
                if (discountOpt.isPresent()) {
                    Discount disc = discountOpt.get();
                    finalPrice = price * (1 - disc.getPercentage() / 100.0);
                    System.out.println("    Discount applied: -" + disc.getPercentage() + "% -> finalPrice=" + finalPrice);
                } else {
                    System.out.println("    No discount applied.");
                }

                double packageQuantity = prod.getGrammage();
                String packageUnit = prod.getUnit();
                double packagePrice = finalPrice;
                double unitPrice = packagePrice / packageQuantity;

                // Normalize units for unit price and display
                double normalizedUnitPrice = unitPrice;
                String normalizedUnit = packageUnit;
                if ("g".equalsIgnoreCase(packageUnit)) {
                    normalizedUnitPrice = unitPrice * 1000;
                    normalizedUnit = "kg";
                }
                if ("ml".equalsIgnoreCase(packageUnit)) {
                    normalizedUnitPrice = unitPrice * 1000;
                    normalizedUnit = "l";
                }

                double quantity = reqItem.getQuantity();
                double cost = packagePrice * quantity;
                String currency = prod.getCurrency();

                total += cost;

                BasketResponse.ItemSummary summary = new BasketResponse.ItemSummary();
                summary.setName(prod.getName());
                summary.setCategory(prod.getCategory());
                summary.setBrand(prod.getBrand());
                summary.setQuantity(quantity);
                summary.setPackageQuantity(packageQuantity);
                summary.setPackageUnit(packageUnit);
                summary.setPackagePrice(packagePrice);
                summary.setUnitPrice(normalizedUnitPrice);
                summary.setUnit(normalizedUnit);
                summary.setCost(cost);
                summary.setCurrency(currency);

                summaries.add(summary);
            }
            if (allAvailable) {
                System.out.println("  All products available in " + store + ". Total: " + total);
                storeLists.put(store, summaries);
            } else {
                System.out.println("  Not all products available in " + store + " for date " + date + ". Skipping store.");
            }
        }

        // === Best unit price logic ===
        Map<String, Double> bestUnitPrices = new HashMap<>();
        for (List<BasketResponse.ItemSummary> itemsList : storeLists.values()) {
            for (BasketResponse.ItemSummary it : itemsList) {
                if (it.getName() == null || it.getUnitPrice() == 0.0) continue;
                bestUnitPrices.merge(it.getName(), it.getUnitPrice(), Math::min);
            }
        }
        for (List<BasketResponse.ItemSummary> itemsList : storeLists.values()) {
            for (BasketResponse.ItemSummary it : itemsList) {
                if (it.getName() == null || it.getUnitPrice() == 0.0) continue;
                boolean isBest = Math.abs(bestUnitPrices.get(it.getName()) - it.getUnitPrice()) < 0.0001;
                it.setBestUnitPrice(isBest);
            }
        }
        // === End of best unit price logic ===

        // --- Find winning store and its total ---
        String winningStore = null;
        double minTotal = Double.MAX_VALUE;
        for (Map.Entry<String, List<BasketResponse.ItemSummary>> entry : storeLists.entrySet()) {
            double storeTotal = entry.getValue().stream().mapToDouble(BasketResponse.ItemSummary::getCost).sum();
            if (storeTotal < minTotal) {
                minTotal = storeTotal;
                winningStore = entry.getKey();
            }
        }

        // --- Build response ---
        BasketResponse resp = new BasketResponse();
        resp.setDate(date);
        resp.setWinningStore(winningStore);
        resp.setTotalCost(minTotal == Double.MAX_VALUE ? 0 : minTotal);

        if (storeLists.isEmpty()) {
            resp.setStoreLists(Collections.emptyMap());
            System.out.println("\nNo store can fulfill the basket for the selected date.");
        } else if (allStores) {
            resp.setStoreLists(storeLists); // All stores
            System.out.println("\nSummary: Basket possible at " + storeLists.keySet() + ". Winning store: " + winningStore);
        } else {
            if (winningStore != null) {
                resp.setStoreLists(Map.of(winningStore, storeLists.get(winningStore)));
                System.out.println("\nSummary: Winning store is " + winningStore);
            } else {
                resp.setStoreLists(Collections.emptyMap());
            }
        }

        System.out.println("=== End of Basket Optimization Debug ===\n");
        return resp;
    }
}
