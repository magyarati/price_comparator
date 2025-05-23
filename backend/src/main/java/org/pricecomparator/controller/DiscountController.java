package org.pricecomparator.controller;

import org.pricecomparator.dto.BestDiscountDto;
import org.pricecomparator.service.DiscountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
public class DiscountController {

    private final DiscountService discountService;

    public DiscountController(DiscountService discountService) {
        this.discountService = discountService;
    }

    @GetMapping("/best")
    public List<BestDiscountDto> bestDiscounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "10") int topN) {
        if (date != null) {
            return discountService.getBestDiscountsByDate(date, topN);
        } else {
            return discountService.getBestDiscounts(topN);
        }
    }
}
