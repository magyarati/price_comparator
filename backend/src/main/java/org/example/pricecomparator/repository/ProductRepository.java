package org.example.pricecomparator.repository;

import org.example.pricecomparator.model.PriceEntry;
import java.util.List;

public interface ProductRepository {
    List<PriceEntry> getPriceHistory(String productId);
}