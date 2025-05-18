package org.example.pricecomparator.controller;

import org.example.pricecomparator.model.Product;
import org.example.pricecomparator.service.ProductService;
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

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAll(@RequestParam(required = false) String date,
                                @RequestParam(required = false) String store) {
        try {
            return productService.getAll(date, store);
        } catch (IOException | CsvValidationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading product data", e);
        }
    }

    @GetMapping("/history")
    public List<Map<String, Object>> getHistory(@RequestParam String store,
                                                @RequestParam String name) {
        return productService.getHistory(store, name);
    }
}
