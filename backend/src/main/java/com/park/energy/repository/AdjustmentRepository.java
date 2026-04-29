package com.park.energy.repository;

import com.park.energy.model.Adjustment;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class AdjustmentRepository {

    private final Map<String, Adjustment> adjustments = new ConcurrentHashMap<>();

    public Adjustment save(Adjustment adjustment) {
        if (adjustment.getId() == null) {
            adjustment.setId(UUID.randomUUID().toString());
        }
        if (adjustment.getCreatedAt() == null) {
            adjustment.setCreatedAt(LocalDateTime.now());
        }
        adjustments.put(adjustment.getId(), adjustment);
        return adjustment;
    }

    public Optional<Adjustment> findById(String id) {
        return Optional.ofNullable(adjustments.get(id));
    }

    public List<Adjustment> findAll() {
        return new ArrayList<>(adjustments.values());
    }

    public List<Adjustment> findByBillId(String billId) {
        return adjustments.values().stream()
                .filter(a -> billId.equals(a.getBillId()))
                .sorted(Comparator.comparing(Adjustment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Adjustment> findByEnterpriseId(String enterpriseId) {
        return adjustments.values().stream()
                .filter(a -> enterpriseId.equals(a.getEnterpriseId()))
                .sorted(Comparator.comparing(Adjustment::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        adjustments.remove(id);
    }
}
