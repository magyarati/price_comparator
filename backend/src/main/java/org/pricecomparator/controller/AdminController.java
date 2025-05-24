package org.pricecomparator.controller;

import org.pricecomparator.repository.InMemoryProductRepository;
import org.pricecomparator.repository.InMemoryDiscountRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final InMemoryProductRepository productRepo;
    private final InMemoryDiscountRepository discountRepo;

    public AdminController(InMemoryProductRepository productRepo, InMemoryDiscountRepository discountRepo) {
        this.productRepo = productRepo;
        this.discountRepo = discountRepo;
    }

    @PostMapping("/reload")
    public String reloadAll() {
        productRepo.reload();
        discountRepo.reload();
        return "Reloaded all product and discount data from CSV files.";
    }
}
