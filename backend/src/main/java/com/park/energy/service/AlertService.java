package com.park.energy.service;

import com.park.energy.model.Alert;
import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.model.UsageStatistics;
import com.park.energy.repository.AlertRepository;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;
    private final UsageStatisticsService usageStatisticsService;

    public AlertService(AlertRepository alertRepository,
                        EnterpriseRepository enterpriseRepository,
                        UsageStatisticsRepository usageStatisticsRepository,
                        UsageStatisticsService usageStatisticsService) {
        this.alertRepository = alertRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.usageStatisticsRepository = usageStatisticsRepository;
        this.usageStatisticsService = usageStatisticsService;
    }

    public Alert createAlert(String enterpriseId, Alert.AlertType type, Alert.AlertLevel level,
                             String title, String description) {
        if (alertRepository.existsActiveAlertByEnterpriseIdAndType(enterpriseId, type)) {
            return null;
        }

        Alert alert = Alert.builder()
                .enterpriseId(enterpriseId)
                .type(type)
                .level(level)
                .title(title)
                .description(description)
                .status(Alert.AlertStatus.ACTIVE)
                .triggeredAt(java.time.LocalDateTime.now())
                .build();

        return alertRepository.save(alert);
    }

    public Alert resolveAlert(String alertId, String resolvedBy) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (!alertOpt.isPresent()) {
            throw new RuntimeException("Alert not found: " + alertId);
        }

        Alert alert = alertOpt.get();
        alert.setStatus(Alert.AlertStatus.RESOLVED);
        alert.setResolvedAt(java.time.LocalDateTime.now());
        alert.setResolvedBy(resolvedBy);

        return alertRepository.save(alert);
    }

    public Alert ignoreAlert(String alertId, String ignoredBy) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (!alertOpt.isPresent()) {
            throw new RuntimeException("Alert not found: " + alertId);
        }

        Alert alert = alertOpt.get();
        alert.setStatus(Alert.AlertStatus.IGNORED);
        alert.setResolvedAt(java.time.LocalDateTime.now());
        alert.setResolvedBy(ignoredBy);

        return alertRepository.save(alert);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }

    public List<Alert> getAlertsByEnterprise(String enterpriseId) {
        return alertRepository.findByEnterpriseId(enterpriseId);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Optional<Alert> getAlertById(String id) {
        return alertRepository.findById(id);
    }

    public List<Alert> checkDailyQuotasAndGenerateAlerts() {
        LocalDate today = LocalDate.now();
        List<Alert> generatedAlerts = new ArrayList<>();

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            usageStatisticsService.aggregateDailyStatistics(
                    enterprise.getId(), Meter.MeterType.ELECTRICITY, today);
            usageStatisticsService.aggregateDailyStatistics(
                    enterprise.getId(), Meter.MeterType.WATER, today);
        }

        for (Enterprise enterprise : enterprises) {
            List<UsageStatistics> stats = usageStatisticsRepository
                    .findByEnterpriseIdAndPeriod(enterprise.getId(), UsageStatistics.StatisticsPeriod.DAILY).stream()
                    .filter(s -> today.equals(s.getPeriodDate()))
                    .collect(Collectors.toList());

            for (UsageStatistics stat : stats) {
                if (stat.getQuotaPercentage() != null && stat.getQuotaPercentage().compareTo(new BigDecimal("90")) >= 0) {
                    String type = stat.getType() == Meter.MeterType.ELECTRICITY ? "用电" : "用水";
                    Alert alert = createAlert(
                            enterprise.getId(),
                            Alert.AlertType.DAILY_QUOTA_WARNING,
                            Alert.AlertLevel.WARNING,
                            type + "日配额预警",
                            String.format("%s日用量已达到配额的%.2f%%，请关注用量情况。",
                                    type, stat.getQuotaPercentage())
                    );
                    if (alert != null) {
                        generatedAlerts.add(alert);
                    }
                }
            }
        }

        return generatedAlerts;
    }

    public List<Alert> checkMonthlyQuotasAndGenerateAlerts(Integer year, Integer month) {
        final int finalYear = year != null ? year : LocalDate.now().getYear();
        final int finalMonth = month != null ? month : LocalDate.now().getMonthValue();

        List<Alert> generatedAlerts = new ArrayList<>();
        List<Enterprise> enterprises = enterpriseRepository.findAll();

        for (Enterprise enterprise : enterprises) {
            usageStatisticsService.aggregateMonthlyStatistics(
                    enterprise.getId(), Meter.MeterType.ELECTRICITY, finalYear, finalMonth);
            usageStatisticsService.aggregateMonthlyStatistics(
                    enterprise.getId(), Meter.MeterType.WATER, finalYear, finalMonth);
        }

        for (Enterprise enterprise : enterprises) {
            List<UsageStatistics> stats = usageStatisticsRepository
                    .findByEnterpriseIdAndPeriod(enterprise.getId(), UsageStatistics.StatisticsPeriod.MONTHLY).stream()
                    .filter(s -> finalYear == s.getYear() && finalMonth == s.getMonth())
                    .collect(Collectors.toList());

            for (UsageStatistics stat : stats) {
                if (stat.getQuotaPercentage() != null && stat.getQuotaPercentage().compareTo(new BigDecimal("100")) > 0) {
                    String type = stat.getType() == Meter.MeterType.ELECTRICITY ? "用电" : "用水";
                    Alert alert = createAlert(
                            enterprise.getId(),
                            Alert.AlertType.MONTHLY_QUOTA_EXCEED,
                            Alert.AlertLevel.CRITICAL,
                            type + "月配额超额",
                            String.format("%s月用量已超过配额的%.2f%%，将按阶梯价格收取超额费用。",
                                    type, stat.getQuotaPercentage())
                    );
                    if (alert != null) {
                        generatedAlerts.add(alert);
                    }
                }
            }
        }

        return generatedAlerts;
    }

    public List<Alert> checkAllQuotasAndGenerateAlerts() {
        List<Alert> generatedAlerts = new ArrayList<>();
        
        List<Alert> dailyAlerts = checkDailyQuotasAndGenerateAlerts();
        generatedAlerts.addAll(dailyAlerts);
        
        List<Alert> monthlyAlerts = checkMonthlyQuotasAndGenerateAlerts(null, null);
        generatedAlerts.addAll(monthlyAlerts);
        
        return generatedAlerts;
    }
}
