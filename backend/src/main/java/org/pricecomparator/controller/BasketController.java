package org.pricecomparator.controller;

import org.pricecomparator.dto.BasketRequest;
import org.pricecomparator.dto.BasketResponse;
import org.pricecomparator.service.BasketService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/basket")
public class BasketController {
    private final BasketService basketService;
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/optimize")
    public BasketResponse optimize(@RequestBody BasketRequest request) {
        return basketService.optimize(request);
    }
}
