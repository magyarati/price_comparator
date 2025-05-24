package org.pricecomparator.repository;

import org.pricecomparator.model.Discount;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscountRepository {
    List<Discount> getAllDiscounts();
    Optional<Discount> findBestDiscount(String productName, String store, LocalDate date);
}
