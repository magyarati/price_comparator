package org.pricecomparator.repository;

import org.pricecomparator.model.Discount;
import java.util.List;

public interface DiscountRepository {
    List<Discount> getAllDiscounts();
}
