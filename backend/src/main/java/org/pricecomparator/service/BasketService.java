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

    public BasketResponse optimizeBasket(List<BasketRequest.Item> items, LocalDate date) {
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
        double minTotal = Double.MAX_VALUE;

        Set<String> allStores = productRepository.getAllStores();
        System.out.println("All stores in repository: " + allStores);

        for (String store : allStores) {
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

                // Use getName() for Discount as per your Discount class
                Optional<Discount> discountOpt = discountRepository.findBestDiscount(prod.getName(), store, date);
                double finalPrice = price;
                if (discountOpt.isPresent()) {
                    Discount disc = discountOpt.get();
                    finalPrice = price * (1 - disc.getPercentage() / 100.0);
                    System.out.println("    Discount applied: -" + disc.getPercentage() + "% -> finalPrice=" + finalPrice);
                } else {
                    System.out.println("    No discount applied.");
                }
                double cost = finalPrice * reqItem.getQuantity();
                System.out.println("    Calculated cost for " + reqItem.getQuantity() + " units: " + cost);

                total += cost;
                BasketResponse.ItemSummary summary = new BasketResponse.ItemSummary();
                summary.setName(prod.getName());
                summary.setQuantity(reqItem.getQuantity());
                summary.setUnitPrice(finalPrice);
                summary.setCost(cost);
                summaries.add(summary);
            }
            if (allAvailable) {
                System.out.println("  All products available in " + store + ". Total: " + total);
                storeLists.put(store, summaries);
                if (total < minTotal) {
                    minTotal = total;
                }
            } else {
                System.out.println("  Not all products available in " + store + " for date " + date + ". Skipping store.");
            }
        }

        BasketResponse resp = new BasketResponse();
        resp.setTotalCost(minTotal == Double.MAX_VALUE ? 0 : minTotal);
        // MAIN CHANGE: Show all stores where basket can be fulfilled
        if (!storeLists.isEmpty()) {
            resp.setStoreLists(storeLists);
            System.out.println("\nSummary: Basket possible at " + storeLists.keySet());
        } else {
            resp.setStoreLists(Collections.emptyMap());
            System.out.println("\nNo store can fulfill the basket for the selected date.");
        }
        resp.setDate(date);
        System.out.println("=== End of Basket Optimization Debug ===\n");
        return resp;
    }
}
