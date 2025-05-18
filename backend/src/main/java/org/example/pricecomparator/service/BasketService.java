package org.example.pricecomparator.service;

import org.example.pricecomparator.dto.BasketRequest;
import org.example.pricecomparator.dto.BasketResponse;
import org.example.pricecomparator.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BasketService {
    private final ProductService productService;

    public BasketService(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Optimize the basket by assigning each requested item to the store offering the lowest
     * effective unit price (= (price * (1 - discount/100)) / grammage).
     */
    public BasketResponse optimize(BasketRequest request) {
        // 1) Fetch all products (today's data across all stores)
        List<Product> allProducts;
        try {
            allProducts = productService.getAll(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve products", e);
        }

        // 2) Group products by lowercase name
        Map<String, List<Product>> productsByName = allProducts.stream()
                .collect(Collectors.groupingBy(p -> p.getName().toLowerCase()));

        BasketResponse response = new BasketResponse();
        response.storeLists = new HashMap<>();
        double totalCost = 0.0;

        // 3) For each requested basket item...
        for (BasketRequest.Item item : request.getItems()) {
            String key = item.getProductName().toLowerCase();
            List<Product> candidates = productsByName.getOrDefault(key, Collections.emptyList());
            if (candidates.isEmpty()) {
                throw new IllegalArgumentException("No product found for: " + item.getProductName());
            }

            // 4) Pick the candidate with the lowest effective unit price
            Product best = candidates.stream()
                    .min(Comparator.comparingDouble(p ->
                            (p.getPrice() * (1 - p.getDiscount() / 100.0)) / p.getGrammage()
                    ))
                    .orElseThrow(() -> new IllegalStateException(
                            "Failed to pick best product for " + item.getProductName()
                    ));

            // 5) Compute line cost and accumulate
            double unitPrice = (best.getPrice() * (1 - best.getDiscount() / 100.0)) / best.getGrammage();
            double lineCost = unitPrice * item.getQuantity();
            totalCost += lineCost;

            // 6) Build and add optimized item
            BasketResponse.OptimizedItem opt = new BasketResponse.OptimizedItem();
            opt.name = best.getName();
            opt.quantity = item.getQuantity();
            opt.unitPrice = unitPrice;
            opt.cost = lineCost;

            response.storeLists
                    .computeIfAbsent(best.getStore(), k -> new ArrayList<>())
                    .add(opt);
        }

        response.totalCost = totalCost;
        return response;
    }
}
