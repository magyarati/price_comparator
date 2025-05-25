package org.pricecomparator.repository;

import org.pricecomparator.model.PriceAlert;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryPriceAlertRepository implements PriceAlertRepository {
    private final List<PriceAlert> alerts = new ArrayList<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public synchronized void addAlert(PriceAlert alert) {
        if (alert.getId() == null) {
            alert.setId(idGen.getAndIncrement());
        }
        alerts.add(alert);
    }

    @Override
    public synchronized List<PriceAlert> getAllAlerts() {
        return new ArrayList<>(alerts);
    }

    @Override
    public synchronized List<PriceAlert> findMatchingAlerts(String productName, String store, double currentPrice) {
        return alerts.stream()
                .filter(a -> a.getProductName().equalsIgnoreCase(productName))
                .filter(a -> a.getStore() == null || a.getStore().isBlank() || a.getStore().equalsIgnoreCase(store))
                .filter(a -> a.getTargetPrice() != null && currentPrice <= a.getTargetPrice())
                .collect(Collectors.toList());
    }

    public synchronized Optional<PriceAlert> findById(Long id) {
        return alerts.stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
    }

    public synchronized boolean deleteById(Long id) {
        return alerts.removeIf(a -> Objects.equals(a.getId(), id));
    }

    public synchronized Optional<PriceAlert> updateAlert(Long id, PriceAlert update) {
        Optional<PriceAlert> alertOpt = findById(id);
        alertOpt.ifPresent(alert -> {
            if (update.getTargetPrice() != null) alert.setTargetPrice(update.getTargetPrice());
            if (update.getStore() != null) alert.setStore(update.getStore());
            if (update.getProductName() != null) alert.setProductName(update.getProductName());
            if (update.getEmail() != null) alert.setEmail(update.getEmail());
        });
        return alertOpt;
    }
}
