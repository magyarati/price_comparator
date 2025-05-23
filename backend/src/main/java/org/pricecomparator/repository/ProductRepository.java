package org.pricecomparator.repository;

import org.pricecomparator.model.Product;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepository {
    /**
     * Get all unique store names.
     */
    Set<String> getAllStores();

    /**
     * Find the latest product entry for a given name/store, available on or before the specified date.
     */
    Optional<Product> findByNameAndStoreAndDate(String name, String store, LocalDate date);

    /**
     * Get all products in the repository.
     */
    List<Product> getAllProducts();

    /**
     * Find the most recent product by store and product name.
     */
    Optional<Product> findByStoreAndProductName(String store, String productName);

    /**
     * Get the price history for a product by product ID (sorted by date).
     */
    List<Product> getPriceHistory(String productId);
}
