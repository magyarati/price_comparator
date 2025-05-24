package org.pricecomparator.controller;

import org.pricecomparator.dto.BasketRequest;
import org.pricecomparator.dto.BasketResponse;
import org.pricecomparator.service.BasketService;
import org.pricecomparator.service.BasketSplitService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/basket")
public class BasketController {

    private final BasketService basketService;
    private final BasketSplitService basketSplitService;

    // ---- SINGLE constructor for both services! ----
    public BasketController(BasketService basketService, BasketSplitService basketSplitService) {
        this.basketService = basketService;
        this.basketSplitService = basketSplitService;
    }

    @PostMapping("/optimize")
    public BasketResponse optimizeBasket(
            @RequestBody BasketRequest request,
            @RequestParam(value = "allStores", defaultValue = "false") boolean allStores
    ) {
        return basketService.optimizeBasket(request.getItems(), request.getDate(), allStores);
    }

    @PostMapping("/optimize-split")
    public BasketResponse optimizeBasketSplit(@RequestBody BasketRequest request) {
        return basketSplitService.optimizeBasketSplit(request.getItems(), request.getDate());
    }
}
