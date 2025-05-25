package org.pricecomparator.service;

import org.pricecomparator.model.PriceAlert;
import org.pricecomparator.model.Product;
import org.pricecomparator.repository.PriceAlertRepository;
import org.pricecomparator.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PriceAlertService {

    private final PriceAlertRepository priceAlertRepository;
    private final ProductRepository productRepository;

    public PriceAlertService(PriceAlertRepository priceAlertRepository, ProductRepository productRepository) {
        this.priceAlertRepository = priceAlertRepository;
        this.productRepository = productRepository;
    }

    public void addAlert(PriceAlert alert) {
        priceAlertRepository.addAlert(alert);
    }

    public List<PriceAlert> getAllAlerts() {
        return priceAlertRepository.getAllAlerts();
    }

    public Optional<PriceAlert> update(Long id, PriceAlert update) {
        return priceAlertRepository.updateAlert(id, update);
    }

    public boolean delete(Long id) {
        return priceAlertRepository.deleteById(id);
    }

    public Optional<PriceAlert> findById(Long id) {
        return priceAlertRepository.findById(id);
    }

    // --- NEW: Check which alerts are currently triggered ---
    public List<PriceAlert> checkAlerts() {
        List<PriceAlert> allAlerts = priceAlertRepository.getAllAlerts();

        return allAlerts.stream()
                .filter(alert -> {
                    // Find product for this alert
                    Optional<Product> prodOpt = productRepository.findByNameAndStoreAndDate(
                            alert.getProductName(),
                            alert.getStore(),
                            java.time.LocalDate.now()
                    );
                    return prodOpt.isPresent()
                            && prodOpt.get().getPrice() <= alert.getTargetPrice();
                })
                .collect(Collectors.toList());
    }
}
