package org.pricecomparator.service;

import org.pricecomparator.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.pricecomparator.model.Product;

import java.util.List;

@Service
public class PriceHistoryService {

    private final ProductRepository productRepository;

    public PriceHistoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getPriceHistoryForProduct(String productId) {
        return productRepository.getPriceHistory(productId);
    }
}
