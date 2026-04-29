package com.park.energy.repository;

import com.park.energy.model.Enterprise;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EnterpriseRepository {

    private final Map<String, Enterprise> enterprises = new ConcurrentHashMap<>();

    public Enterprise save(Enterprise enterprise) {
        if (enterprise.getId() == null) {
            enterprise.setId(UUID.randomUUID().toString());
        }
        if (enterprise.getCreatedAt() == null) {
            enterprise.setCreatedAt(LocalDateTime.now());
        }
        enterprise.setUpdatedAt(LocalDateTime.now());
        enterprises.put(enterprise.getId(), enterprise);
        return enterprise;
    }

    public Optional<Enterprise> findById(String id) {
        return Optional.ofNullable(enterprises.get(id));
    }

    public List<Enterprise> findAll() {
        return new ArrayList<>(enterprises.values());
    }

    public List<Enterprise> findByIndustryType(String industryType) {
        return enterprises.values().stream()
                .filter(e -> industryType.equals(e.getIndustryType()))
                .collect(Collectors.toList());
    }

    public List<Enterprise> findKeyEnterprises() {
        return enterprises.values().stream()
                .filter(Enterprise::getKeyEnterprise)
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        enterprises.remove(id);
    }

    public boolean existsById(String id) {
        return enterprises.containsKey(id);
    }
}
