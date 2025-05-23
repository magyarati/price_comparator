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
public class BasketSplitService {

    private final InMemoryProductRepository productRepository;
    private final InMemoryDiscountRepository discountRepository;

    public BasketSplitService(InMemoryProductRepository productRepository, InMemoryDiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    /**
     * Optimizes a basket by buying each product from the store with the lowest price.
     * Returns a map of shopping lists per store.
     */
    public BasketResponse optimizeBasketSplit(List<BasketRequest.Item> items, LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        System.out.println("\n=== Split Basket Debug ===");
        System.out.println("Optimizing split basket for date: " + date);
        System.out.println("Basket items:");
        for (BasketRequest.Item reqItem : items) {
            System.out.println("- " + reqItem.getProductName() + ", qty=" + reqItem.getQuantity());
        }

        Map<String, List<BasketResponse.ItemSummary>> storeLists = new HashMap<>();
        double totalCost = 0;

        for (BasketRequest.Item reqItem : items) {
            double bestPrice = Double.MAX_VALUE;
            String bestStore = null;
            Product bestProduct = null;

            System.out.println("\nProduct: " + reqItem.getProductName());

            for (String store : productRepository.getAllStores()) {
                Optional<Product> prodOpt = productRepository.findByNameAndStoreAndDate(reqItem.getProductName(), store, date);
                if (prodOpt.isEmpty()) {
                    System.out.println("  NOT found in " + store + " for date " + date);
                    continue;
                }
                Product prod = prodOpt.get();
                double price = prod.getPrice();

                Optional<Discount> discountOpt = discountRepository.findBestDiscount(prod.getName(), store, date);
                double finalPrice = price;
                if (discountOpt.isPresent()) {
                    Discount disc = discountOpt.get();
                    finalPrice = price * (1 - disc.getPercentage() / 100.0);
                    System.out.printf("  Found in %s: base price=%.2f, discount=%.2f%%, final price=%.2f\n",
                            store, price, disc.getPercentage(), finalPrice);
                } else {
                    System.out.printf("  Found in %s: price=%.2f, no discount\n", store, price);
                }
                if (finalPrice < bestPrice) {
                    bestPrice = finalPrice;
                    bestStore = store;
                    bestProduct = prod;
                }
            }

            if (bestStore != null && bestProduct != null) {
                double cost = bestPrice * reqItem.getQuantity();
                totalCost += cost;

                System.out.printf("  => Best option: %s at %.2f/unit, cost for %.2f: %.2f\n", bestStore, bestPrice, reqItem.getQuantity(), cost);

                BasketResponse.ItemSummary summary = new BasketResponse.ItemSummary();
                summary.setName(bestProduct.getName());
                summary.setQuantity(reqItem.getQuantity());
                summary.setUnitPrice(bestPrice);
                summary.setCost(cost);

                storeLists.computeIfAbsent(bestStore, k -> new ArrayList<>()).add(summary);
            } else {
                System.out.println("  => No store can provide this product on " + date);
            }
        }

        System.out.println("\nSplit Shopping List by Store:");
        for (Map.Entry<String, List<BasketResponse.ItemSummary>> entry : storeLists.entrySet()) {
            double storeTotal = entry.getValue().stream().mapToDouble(BasketResponse.ItemSummary::getCost).sum();
            System.out.printf("- %s: %d products, subtotal %.2f\n", entry.getKey(), entry.getValue().size(), storeTotal);
            for (BasketResponse.ItemSummary summary : entry.getValue()) {
                System.out.printf("    * %s x%.2f @ %.2f = %.2f\n",
                        summary.getName(), summary.getQuantity(), summary.getUnitPrice(), summary.getCost());
            }
        }
        System.out.printf("Total estimated split basket cost: %.2f\n", totalCost);
        System.out.println("=== End Split Basket Debug ===\n");

        BasketResponse resp = new BasketResponse();
        resp.setTotalCost(totalCost);
        resp.setStoreLists(storeLists);
        resp.setDate(date);
        return resp;
    }
}
