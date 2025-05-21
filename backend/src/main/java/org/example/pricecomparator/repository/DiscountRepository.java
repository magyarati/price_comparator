package org.example.pricecomparator.repository;

import org.example.pricecomparator.model.Discount;
import java.util.List;

public interface DiscountRepository {
    List<Discount> getAllDiscounts();
}
