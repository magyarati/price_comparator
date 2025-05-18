package org.example.pricecomparator;

import org.example.pricecomparator.model.Product;
import org.example.pricecomparator.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    void testGetAllReturnsNonNull() throws Exception {
        List<Product> list = productService.getAll("2025-05-08", null);
        assertNotNull(list);
        assertFalse(list.isEmpty(), "Product list should not be empty");
    }

    @Test
    void testHistoryContainsDates() throws Exception {
        List<Product> all = productService.getAll("2025-05-08", "lidl");
        assertNotNull(all);
        assertFalse(all.isEmpty(), "Product list should not be empty");

        Product p = all.get(0);
        List<?> hist = productService.getHistory("lidl", p.getName());
        assertNotNull(hist);
        assertFalse(hist.isEmpty(), "History should contain data");
    }
}
