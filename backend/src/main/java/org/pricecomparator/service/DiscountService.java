package org.pricecomparator.service;

import org.pricecomparator.dto.BestDiscountDto;
import org.pricecomparator.model.Discount;
import org.pricecomparator.model.Product;
import org.pricecomparator.repository.DiscountRepository;
import org.pricecomparator.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;

    public DiscountService(DiscountRepository discountRepository, ProductRepository productRepository) {
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    public List<BestDiscountDto> getBestDiscounts(int topN) {
        return discountRepository.getAllDiscounts().stream()
                .sorted(Comparator.comparingDouble(Discount::getPercentage).reversed())
                .limit(topN)
                .map(discount -> {
                    Optional<Product> product = productRepository
                            .findByStoreAndProductName(discount.getStore(), discount.getProductName());
                    return BestDiscountDto.fromDiscountAndProduct(discount, product.orElse(null));
                })
                .collect(Collectors.toList());
    }

    public List<BestDiscountDto> getBestDiscountsByDate(LocalDate date, int topN) {
        return discountRepository.getAllDiscounts().stream()
                .filter(discount -> !date.isBefore(discount.getValidFrom()) && !date.isAfter(discount.getValidUntil()))
                .sorted(Comparator.comparingDouble(Discount::getPercentage).reversed())
                .limit(topN)
                .map(discount -> {
                    Optional<Product> product = productRepository
                            .findByStoreAndProductName(discount.getStore(), discount.getProductName());
                    return BestDiscountDto.fromDiscountAndProduct(discount, product.orElse(null));
                })
                .collect(Collectors.toList());
    }
}
