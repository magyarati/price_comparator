package org.pricecomparator.controller;

import org.pricecomparator.service.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.pricecomparator.model.Product;

import java.util.List;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<Product>> getPriceHistory(@PathVariable String productId) {
        List<Product> history = priceHistoryService.getPriceHistoryForProduct(productId);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}
