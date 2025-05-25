package org.pricecomparator.repository;

import org.pricecomparator.model.PriceAlert;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository {
    void addAlert(PriceAlert alert);
    List<PriceAlert> getAllAlerts();
    List<PriceAlert> findMatchingAlerts(String productName, String store, double currentPrice);

    // New methods for full CRUD support:
    Optional<PriceAlert> findById(Long id);

    boolean deleteById(Long id);

    Optional<PriceAlert> updateAlert(Long id, PriceAlert update);
}
