package org.pricecomparator.controller;

import org.pricecomparator.model.PriceAlert;
import org.pricecomparator.service.PriceAlertService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class PriceAlertController {
    private final PriceAlertService alertService;

    public PriceAlertController(PriceAlertService alertService) {
        this.alertService = alertService;
    }

    // Set a new price alert
    @PostMapping
    public void addAlert(@RequestBody PriceAlert alert) {
        alertService.addAlert(alert);
    }

    // View all alerts (for demo/testing)
    @GetMapping
    public List<PriceAlert> getAll() {
        return alertService.getAllAlerts();
    }

    // Check which alerts are currently "active"
    @GetMapping("/triggered")
    public List<PriceAlert> checkAlerts() {
        return alertService.checkAlerts();
    }

    @DeleteMapping("/{id}")
    public void deleteAlert(@PathVariable Long id) {
        if (!alertService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found");
        }
    }

    @PutMapping("/{id}")
    public PriceAlert updateAlert(@PathVariable Long id, @RequestBody PriceAlert update) {
        return alertService.update(id, update)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alert not found"));
    }
}
