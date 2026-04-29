package com.park.energy.service;

import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.model.UsageStatistics;
import com.park.energy.model.EnergyAssessment;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import com.park.energy.repository.EnergyAssessmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnergyAssessmentService {

    private final EnergyAssessmentRepository assessmentRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;

    public EnergyAssessmentService(EnergyAssessmentRepository assessmentRepository,
                                   EnterpriseRepository enterpriseRepository,
                                   UsageStatisticsRepository usageStatisticsRepository) {
        this.assessmentRepository = assessmentRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.usageStatisticsRepository = usageStatisticsRepository;
    }

    public EnergyAssessment assessEnergySaving(String enterpriseId, Integer year, Integer month) {
        Optional<EnergyAssessment> existing = assessmentRepository
                .findByEnterpriseIdAndYearMonth(enterpriseId, year, month);
        if (existing.isPresent()) {
            return existing.get();
        }

        Optional<Enterprise> enterpriseOpt = enterpriseRepository.findById(enterpriseId);
        if (!enterpriseOpt.isPresent()) {
            throw new RuntimeException("Enterprise not found: " + enterpriseId);
        }

        Enterprise enterprise = enterpriseOpt.get();

        List<UsageStatistics> currentStats = usageStatisticsRepository
                .findByEnterpriseIdAndPeriod(enterpriseId, UsageStatistics.StatisticsPeriod.MONTHLY).stream()
                .filter(s -> year.equals(s.getYear()) && month.equals(s.getMonth()))
                .collect(Collectors.toList());

        BigDecimal currentElectricity = currentStats.stream()
                .filter(s -> Meter.MeterType.ELECTRICITY == s.getType())
                .findFirst()
                .map(UsageStatistics::getConsumption)
                .orElse(BigDecimal.ZERO);

        BigDecimal currentWater = currentStats.stream()
                .filter(s -> Meter.MeterType.WATER == s.getType())
                .findFirst()
                .map(UsageStatistics::getConsumption)
                .orElse(BigDecimal.ZERO);

        BigDecimal monthlyElectricityQuota = enterprise.getMonthlyElectricityQuota();
        BigDecimal monthlyWaterQuota = enterprise.getMonthlyWaterQuota();

        BigDecimal electricitySavingRate = calculateSavingRate(currentElectricity, monthlyElectricityQuota);
        BigDecimal waterSavingRate = calculateSavingRate(currentWater, monthlyWaterQuota);

        BigDecimal overallSavingRate = electricitySavingRate.multiply(new BigDecimal("0.7"))
                .add(waterSavingRate.multiply(new BigDecimal("0.3")));

        EnergyAssessment.AssessmentLevel level = determineAssessmentLevel(overallSavingRate);

        EnergyAssessment assessment = EnergyAssessment.builder()
                .enterpriseId(enterpriseId)
                .year(year)
                .month(month)
                .electricityConsumption(currentElectricity)
                .waterConsumption(currentWater)
                .electricityQuota(monthlyElectricityQuota)
                .waterQuota(monthlyWaterQuota)
                .electricitySavingRate(electricitySavingRate)
                .waterSavingRate(waterSavingRate)
                .overallSavingRate(overallSavingRate)
                .level(level)
                .eligibleForDiscount(isEligibleForDiscount(level))
                .assessedAt(LocalDate.now())
                .build();

        return assessmentRepository.save(assessment);
    }

    private BigDecimal calculateSavingRate(BigDecimal actualUsage, BigDecimal quota) {
        if (quota == null || quota.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (actualUsage.compareTo(quota) >= 0) {
            return BigDecimal.ZERO;
        }
        return quota.subtract(actualUsage)
                .divide(quota, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    private EnergyAssessment.AssessmentLevel determineAssessmentLevel(BigDecimal savingRate) {
        if (savingRate.compareTo(new BigDecimal("30")) >= 0) {
            return EnergyAssessment.AssessmentLevel.EXCELLENT;
        } else if (savingRate.compareTo(new BigDecimal("20")) >= 0) {
            return EnergyAssessment.AssessmentLevel.GOOD;
        } else if (savingRate.compareTo(new BigDecimal("10")) >= 0) {
            return EnergyAssessment.AssessmentLevel.PASS;
        } else {
            return EnergyAssessment.AssessmentLevel.FAIL;
        }
    }

    private boolean isEligibleForDiscount(EnergyAssessment.AssessmentLevel level) {
        return level == EnergyAssessment.AssessmentLevel.EXCELLENT
                || level == EnergyAssessment.AssessmentLevel.GOOD
                || level == EnergyAssessment.AssessmentLevel.PASS;
    }

    public List<EnergyAssessment> getAssessmentsByEnterprise(String enterpriseId) {
        return assessmentRepository.findByEnterpriseId(enterpriseId);
    }

    public List<EnergyAssessment> getAssessmentsByYearMonth(Integer year, Integer month) {
        return assessmentRepository.findByYearMonth(year, month);
    }

    public List<EnergyAssessment> getAllAssessments() {
        return assessmentRepository.findAll();
    }

    public Optional<EnergyAssessment> getAssessmentById(String id) {
        return assessmentRepository.findById(id);
    }

    public void assessAllEnterprises(Integer year, Integer month) {
        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            assessEnergySaving(enterprise.getId(), year, month);
        }
    }

    public EnergyAssessment assessEnterprise(String enterpriseId, Integer year, Integer month) {
        return assessEnergySaving(enterpriseId, year, month);
    }

    public List<EnergyAssessment> getQualifiedAssessments(Integer year, Integer month) {
        return assessmentRepository.findByYearMonth(year, month).stream()
                .filter(a -> Boolean.TRUE.equals(a.getEligibleForDiscount()))
                .collect(Collectors.toList());
    }
}
