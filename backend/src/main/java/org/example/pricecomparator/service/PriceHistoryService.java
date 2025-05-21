package org.example.pricecomparator.service;

import org.example.pricecomparator.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.example.pricecomparator.model.Product;

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
