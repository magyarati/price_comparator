package org.example.pricecomparator.repository;

import org.example.pricecomparator.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getAllProducts();
    Optional<Product> findByStoreAndProductName(String store, String productName);
    List<Product> getPriceHistory(String productId);
}
