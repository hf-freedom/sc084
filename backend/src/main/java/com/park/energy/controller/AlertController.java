package com.park.energy.controller;

import com.park.energy.model.Alert;
import com.park.energy.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable String id) {
        Optional<Alert> alert = alertService.getAlertById(id);
        return alert.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public List<Alert> getActiveAlerts() {
        return alertService.getActiveAlerts();
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<Alert> getAlertsByEnterprise(@PathVariable String enterpriseId) {
        return alertService.getAlertsByEnterprise(enterpriseId);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<Alert> resolveAlert(
            @PathVariable String id,
            @RequestParam(required = false) String resolvedBy) {
        try {
            Alert resolved = alertService.resolveAlert(id, resolvedBy != null ? resolvedBy : "system");
            return ResponseEntity.ok(resolved);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/ignore")
    public ResponseEntity<Alert> ignoreAlert(
            @PathVariable String id,
            @RequestParam(required = false) String ignoredBy) {
        try {
            Alert ignored = alertService.ignoreAlert(id, ignoredBy != null ? ignoredBy : "system");
            return ResponseEntity.ok(ignored);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/generate-daily")
    public List<Alert> generateDailyQuotaAlerts() {
        return alertService.checkDailyQuotasAndGenerateAlerts();
    }

    @PostMapping("/generate-monthly")
    public List<Alert> generateMonthlyQuotaAlerts(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return alertService.checkMonthlyQuotasAndGenerateAlerts(year, month);
    }

    @PostMapping("/generate-all")
    public List<Alert> generateAllQuotaAlerts() {
        return alertService.checkAllQuotasAndGenerateAlerts();
    }
}
