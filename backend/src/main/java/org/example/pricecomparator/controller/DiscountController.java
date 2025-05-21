package org.example.pricecomparator.controller;

import org.example.pricecomparator.dto.BestDiscountDto;
import org.example.pricecomparator.service.DiscountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/best")
    public List<BestDiscountDto> getBestDiscounts(
            @RequestParam(defaultValue = "10") int topN) {
        return discountService.getBestDiscounts(topN);
    }
}
