package org.pricecomparator.controller;

import org.pricecomparator.dto.BasketRequest;
import org.pricecomparator.dto.BasketResponse;
import org.pricecomparator.model.Product;
import org.pricecomparator.service.BasketSplitService;
import org.pricecomparator.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    private final BasketSplitService basketSplitService;

    public ProductController(ProductService productService, BasketSplitService basketSplitService) {
        this.productService = productService;
        this.basketSplitService = basketSplitService;
    }

    // Endpoint to get all products, optionally filtered by date and store
    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) String date,
                                @RequestParam(required = false) String store) {
        try {
            return productService.getAll(date, store);
        } catch (IOException | CsvValidationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading product data", e);
        }
    }

    // Flexible price history endpoint for both React and HTML
    @GetMapping("/history")
    public List<Map<String, Object>> getHistory(
            @RequestParam String name,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category
    ) {
        return productService.getHistory(name, store, brand, category);
    }

    // Basket optimization endpoint
    @PostMapping("/optimize-split")
    public BasketResponse optimizeBasketSplit(@RequestBody BasketRequest request) {
        return basketSplitService.optimizeBasketSplit(request.getItems(), request.getDate());
    }
}
