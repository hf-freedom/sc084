package com.park.energy.repository;

import com.park.energy.model.Meter;
import com.park.energy.model.MeterReading;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class MeterReadingRepository {

    private final Map<String, MeterReading> readings = new ConcurrentHashMap<>();

    public MeterReading save(MeterReading reading) {
        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID().toString());
        }
        if (reading.getCreatedAt() == null) {
            reading.setCreatedAt(LocalDateTime.now());
        }
        readings.put(reading.getId(), reading);
        return reading;
    }

    public Optional<MeterReading> findById(String id) {
        return Optional.ofNullable(readings.get(id));
    }

    public List<MeterReading> findAll() {
        return new ArrayList<>(readings.values());
    }

    public List<MeterReading> findByMeterId(String meterId) {
        return readings.values().stream()
                .filter(r -> meterId.equals(r.getMeterId()))
                .sorted(Comparator.comparing(MeterReading::getReadingTime).reversed())
                .collect(Collectors.toList());
    }

    public List<MeterReading> findByEnterpriseId(String enterpriseId) {
        return readings.values().stream()
                .filter(r -> enterpriseId.equals(r.getEnterpriseId()))
                .sorted(Comparator.comparing(MeterReading::getReadingTime).reversed())
                .collect(Collectors.toList());
    }

    public List<MeterReading> findByEnterpriseIdAndType(String enterpriseId, Meter.MeterType type) {
        return readings.values().stream()
                .filter(r -> enterpriseId.equals(r.getEnterpriseId()) && type == r.getType())
                .sorted(Comparator.comparing(MeterReading::getReadingTime).reversed())
                .collect(Collectors.toList());
    }

    public List<MeterReading> findByReadingTimeBetween(LocalDateTime start, LocalDateTime end) {
        return readings.values().stream()
                .filter(r -> !r.getReadingTime().isBefore(start) && !r.getReadingTime().isAfter(end))
                .sorted(Comparator.comparing(MeterReading::getReadingTime))
                .collect(Collectors.toList());
    }

    public Optional<MeterReading> findLatestByMeterId(String meterId) {
        return readings.values().stream()
                .filter(r -> meterId.equals(r.getMeterId()))
                .max(Comparator.comparing(MeterReading::getReadingTime));
    }

    public void deleteById(String id) {
        readings.remove(id);
    }
}
