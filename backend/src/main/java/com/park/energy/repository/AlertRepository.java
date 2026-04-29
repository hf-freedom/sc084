package com.park.energy.repository;

import com.park.energy.model.Alert;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AlertRepository {

    private final Map<String, Alert> alerts = new ConcurrentHashMap<>();

    public Alert save(Alert alert) {
        if (alert.getId() == null) {
            alert.setId(UUID.randomUUID().toString());
        }
        if (alert.getCreatedAt() == null) {
            alert.setCreatedAt(LocalDateTime.now());
        }
        alerts.put(alert.getId(), alert);
        return alert;
    }

    public Optional<Alert> findById(String id) {
        return Optional.ofNullable(alerts.get(id));
    }

    public List<Alert> findAll() {
        return new ArrayList<>(alerts.values());
    }

    public List<Alert> findByEnterpriseId(String enterpriseId) {
        return alerts.values().stream()
                .filter(a -> enterpriseId.equals(a.getEnterpriseId()))
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Alert> findByStatus(Alert.AlertStatus status) {
        return alerts.values().stream()
                .filter(a -> status == a.getStatus())
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Alert> findActiveAlerts() {
        return alerts.values().stream()
                .filter(a -> Alert.AlertStatus.ACTIVE == a.getStatus())
                .sorted(Comparator.comparing(Alert::getTriggeredAt).reversed())
                .collect(Collectors.toList());
    }

    public boolean existsActiveAlertByEnterpriseIdAndType(String enterpriseId, Alert.AlertType type) {
        return alerts.values().stream()
                .anyMatch(a -> enterpriseId.equals(a.getEnterpriseId())
                        && type == a.getType()
                        && Alert.AlertStatus.ACTIVE == a.getStatus());
    }

    public void deleteById(String id) {
        alerts.remove(id);
    }
}
