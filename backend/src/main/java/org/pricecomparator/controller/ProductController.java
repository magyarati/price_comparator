package org.pricecomparator.controller;

import org.pricecomparator.model.Product;
import org.pricecomparator.service.ProductService;
import org.springframework.web.bind.annotation.*;

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
    public List<Product> getAll(
            @RequestParam(required = false) List<String> stores,
            @RequestParam(required = false) List<String> names,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return productService.getAllFiltered(stores, names, brands, date, startDate, endDate);
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

}
