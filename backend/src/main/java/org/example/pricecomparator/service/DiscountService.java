package org.example.pricecomparator.service;

import org.example.pricecomparator.dto.BestDiscountDto;
import org.example.pricecomparator.model.Discount;
import org.example.pricecomparator.model.Product;
import org.example.pricecomparator.repository.DiscountRepository;
import org.example.pricecomparator.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DiscountService {
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public DiscountService(ProductRepository productRepository, DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public List<BestDiscountDto> getBestDiscounts(int topN) {
        List<Discount> discounts = discountRepository.getAllDiscounts();
        List<BestDiscountDto> bestDiscounts = new ArrayList<>();

        for (Discount discount : discounts) {
            Optional<Product> productOpt = productRepository.findByStoreAndProductName(
                    discount.getStore(), discount.getProductName());
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                double originalPrice = product.getPrice();

                // Calculate discounted price from percentage if not in the Discount record
                double discountedPrice = discount.getDiscountedPrice() > 0
                        ? discount.getDiscountedPrice()
                        : originalPrice * (1 - discount.getPercentage() / 100.0);

                if (originalPrice > 0 && discountedPrice < originalPrice) {
                    double discountPercent = ((originalPrice - discountedPrice) / originalPrice) * 100.0;
                    bestDiscounts.add(new BestDiscountDto(
                            discount.getStore(),
                            discount.getProductName(),
                            product.getCategory(),
                            product.getBrand(),
                            originalPrice,
                            Math.round(discountedPrice * 100.0) / 100.0,
                            Math.round(discountPercent * 100.0) / 100.0
                    ));
                }
            }
        }

        return bestDiscounts.stream()
                .sorted(Comparator.comparingDouble(BestDiscountDto::getDiscountPercent).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }
}
