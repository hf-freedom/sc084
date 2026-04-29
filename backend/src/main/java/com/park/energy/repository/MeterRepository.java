package com.park.energy.repository;

import com.park.energy.model.Meter;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MeterRepository {

    private final Map<String, Meter> meters = new ConcurrentHashMap<>();

    public Meter save(Meter meter) {
        if (meter.getId() == null) {
            meter.setId(UUID.randomUUID().toString());
        }
        if (meter.getCreatedAt() == null) {
            meter.setCreatedAt(LocalDateTime.now());
        }
        meters.put(meter.getId(), meter);
        return meter;
    }

    public Optional<Meter> findById(String id) {
        return Optional.ofNullable(meters.get(id));
    }

    public List<Meter> findAll() {
        return new ArrayList<>(meters.values());
    }

    public List<Meter> findByEnterpriseId(String enterpriseId) {
        return meters.values().stream()
                .filter(m -> enterpriseId.equals(m.getEnterpriseId()))
                .collect(Collectors.toList());
    }

    public List<Meter> findByEnterpriseIdAndType(String enterpriseId, Meter.MeterType type) {
        return meters.values().stream()
                .filter(m -> enterpriseId.equals(m.getEnterpriseId()) && type == m.getType())
                .collect(Collectors.toList());
    }

    public List<Meter> findActiveMeters() {
        return meters.values().stream()
                .filter(Meter::getActive)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        meters.remove(id);
    }

    public boolean existsById(String id) {
        return meters.containsKey(id);
    }
}
