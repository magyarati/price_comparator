package org.example.pricecomparator.controller;

import org.example.pricecomparator.model.PriceEntry;
import org.example.pricecomparator.service.PriceHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/price-history")
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    public PriceHistoryController(PriceHistoryService priceHistoryService) {
        this.priceHistoryService = priceHistoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<PriceEntry>> getPriceHistory(@PathVariable String productId) {
        List<PriceEntry> history = priceHistoryService.getPriceHistoryForProduct(productId);
        if (history.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(history);
    }
}
