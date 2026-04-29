package com.park.energy.repository;

import com.park.energy.model.EnergyAssessment;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EnergyAssessmentRepository {

    private final Map<String, EnergyAssessment> assessments = new ConcurrentHashMap<>();

    public EnergyAssessment save(EnergyAssessment assessment) {
        if (assessment.getId() == null) {
            assessment.setId(UUID.randomUUID().toString());
        }
        if (assessment.getCreatedAt() == null) {
            assessment.setCreatedAt(LocalDate.now());
        }
        assessments.put(assessment.getId(), assessment);
        return assessment;
    }

    public Optional<EnergyAssessment> findById(String id) {
        return Optional.ofNullable(assessments.get(id));
    }

    public List<EnergyAssessment> findAll() {
        return new ArrayList<>(assessments.values());
    }

    public List<EnergyAssessment> findByEnterpriseId(String enterpriseId) {
        return assessments.values().stream()
                .filter(a -> enterpriseId.equals(a.getEnterpriseId()))
                .sorted(Comparator.comparing(EnergyAssessment::getYear).reversed()
                        .thenComparing(EnergyAssessment::getMonth).reversed())
                .collect(Collectors.toList());
    }

    public Optional<EnergyAssessment> findByEnterpriseIdAndYearMonth(String enterpriseId, Integer year, Integer month) {
        return assessments.values().stream()
                .filter(a -> enterpriseId.equals(a.getEnterpriseId())
                        && year.equals(a.getYear())
                        && month.equals(a.getMonth()))
                .findFirst();
    }

    public List<EnergyAssessment> findByYearMonth(Integer year, Integer month) {
        return assessments.values().stream()
                .filter(a -> year.equals(a.getYear()) && month.equals(a.getMonth()))
                .collect(Collectors.toList());
    }

    public List<EnergyAssessment> findQualifiedByYearMonth(Integer year, Integer month) {
        return assessments.values().stream()
                .filter(a -> year.equals(a.getYear())
                        && month.equals(a.getMonth())
                        && Boolean.TRUE.equals(a.getQualified()))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        assessments.remove(id);
    }
}
