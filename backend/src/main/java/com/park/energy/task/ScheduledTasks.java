package com.park.energy.task;

import com.park.energy.model.Alert;
import com.park.energy.model.Enterprise;
import com.park.energy.model.InspectionTask;
import com.park.energy.model.Meter;
import com.park.energy.model.ResampleTask;
import com.park.energy.model.UsageStatistics;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.MeterReadingRepository;
import com.park.energy.repository.MeterRepository;
import com.park.energy.repository.ResampleTaskRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import com.park.energy.service.AlertService;
import com.park.energy.service.BillService;
import com.park.energy.service.EnergyAssessmentService;
import com.park.energy.service.InspectionTaskService;
import com.park.energy.service.MeterReadingService;
import com.park.energy.service.ParkRankingService;
import com.park.energy.service.UsageStatisticsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private final MeterRepository meterRepository;
    private final MeterReadingService meterReadingService;
    private final UsageStatisticsService usageStatisticsService;
    private final AlertService alertService;
    private final BillService billService;
    private final EnergyAssessmentService energyAssessmentService;
    private final ParkRankingService parkRankingService;
    private final InspectionTaskService inspectionTaskService;
    private final EnterpriseRepository enterpriseRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;
    private final ResampleTaskRepository resampleTaskRepository;
    private final MeterReadingRepository meterReadingRepository;

    private final Random random = new Random();

    public ScheduledTasks(MeterRepository meterRepository,
                          MeterReadingService meterReadingService,
                          UsageStatisticsService usageStatisticsService,
                          AlertService alertService,
                          BillService billService,
                          EnergyAssessmentService energyAssessmentService,
                          ParkRankingService parkRankingService,
                          InspectionTaskService inspectionTaskService,
                          EnterpriseRepository enterpriseRepository,
                          UsageStatisticsRepository usageStatisticsRepository,
                          ResampleTaskRepository resampleTaskRepository,
                          MeterReadingRepository meterReadingRepository) {
        this.meterRepository = meterRepository;
        this.meterReadingService = meterReadingService;
        this.usageStatisticsService = usageStatisticsService;
        this.alertService = alertService;
        this.billService = billService;
        this.energyAssessmentService = energyAssessmentService;
        this.parkRankingService = parkRankingService;
        this.inspectionTaskService = inspectionTaskService;
        this.enterpriseRepository = enterpriseRepository;
        this.usageStatisticsRepository = usageStatisticsRepository;
        this.resampleTaskRepository = resampleTaskRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void simulateMeterReadings() {
        List<Meter> activeMeters = meterRepository.findActiveMeters();
        for (Meter meter : activeMeters) {
            if (random.nextDouble() > 0.05) {
                BigDecimal increment;
                if (meter.getType() == Meter.MeterType.ELECTRICITY) {
                    increment = new BigDecimal(random.nextDouble() * 50 + 10).setScale(2, RoundingMode.HALF_UP);
                } else {
                    increment = new BigDecimal(random.nextDouble() * 5 + 1).setScale(2, RoundingMode.HALF_UP);
                }

                BigDecimal newReading = meter.getLastReading().add(increment);
                try {
                    meterReadingService.submitReading(meter.getId(), newReading);
                } catch (Exception e) {
                    // Ignore errors
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void aggregateHourlyStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentHour = now.withMinute(0).withSecond(0).withNano(0);

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            usageStatisticsService.aggregateHourlyStatistics(
                    enterprise.getId(), Meter.MeterType.ELECTRICITY, currentHour);
            usageStatisticsService.aggregateHourlyStatistics(
                    enterprise.getId(), Meter.MeterType.WATER, currentHour);
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void aggregateDailyStatistics() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            usageStatisticsService.aggregateDailyStatistics(
                    enterprise.getId(), Meter.MeterType.ELECTRICITY, yesterday);
            usageStatisticsService.aggregateDailyStatistics(
                    enterprise.getId(), Meter.MeterType.WATER, yesterday);
        }

        checkDailyQuotaAndCreateAlerts(yesterday);
        checkAbnormalFluctuation();
    }

    private void checkDailyQuotaAndCreateAlerts(LocalDate date) {
        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            List<UsageStatistics> stats = usageStatisticsRepository
                    .findByEnterpriseIdAndPeriod(enterprise.getId(), UsageStatistics.StatisticsPeriod.DAILY).stream()
                    .filter(s -> date.equals(s.getPeriodDate()))
                    .collect(Collectors.toList());

            for (UsageStatistics stat : stats) {
                if (stat.getQuotaPercentage() != null && stat.getQuotaPercentage().compareTo(new BigDecimal("90")) >= 0) {
                    String type = stat.getType() == Meter.MeterType.ELECTRICITY ? "用电" : "用水";
                    alertService.createAlert(
                            enterprise.getId(),
                            Alert.AlertType.DAILY_QUOTA_WARNING,
                            Alert.AlertLevel.WARNING,
                            type + "日配额预警",
                            String.format("%s日用量已达到配额的%.2f%%，请关注用量情况。",
                                    type, stat.getQuotaPercentage())
                    );
                }
            }
        }
    }

    private void checkAbnormalFluctuation() {
        List<Enterprise> keyEnterprises = enterpriseRepository.findKeyEnterprises();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        for (Enterprise enterprise : keyEnterprises) {
            checkFluctuationForType(enterprise, Meter.MeterType.ELECTRICITY, yesterday, twoDaysAgo);
            checkFluctuationForType(enterprise, Meter.MeterType.WATER, yesterday, twoDaysAgo);
        }
    }

    private void checkFluctuationForType(Enterprise enterprise, Meter.MeterType type,
                                          LocalDate yesterday, LocalDate twoDaysAgo) {
        List<UsageStatistics> yesterdayCandidates = usageStatisticsRepository
                .findByEnterpriseIdAndPeriodAndDate(enterprise.getId(), UsageStatistics.StatisticsPeriod.DAILY, yesterday);
        Optional<UsageStatistics> yesterdayStat = yesterdayCandidates.stream()
                .filter(s -> type == s.getType())
                .findFirst();

        List<UsageStatistics> twoDaysAgoCandidates = usageStatisticsRepository
                .findByEnterpriseIdAndPeriodAndDate(enterprise.getId(), UsageStatistics.StatisticsPeriod.DAILY, twoDaysAgo);
        Optional<UsageStatistics> twoDaysAgoStat = twoDaysAgoCandidates.stream()
                .filter(s -> type == s.getType())
                .findFirst();

        if (yesterdayStat.isPresent() && twoDaysAgoStat.isPresent()) {
            BigDecimal yesterdayConsumption = yesterdayStat.get().getConsumption();
            BigDecimal twoDaysAgoConsumption = twoDaysAgoStat.get().getConsumption();

            if (twoDaysAgoConsumption.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal fluctuation = yesterdayConsumption.subtract(twoDaysAgoConsumption)
                        .divide(twoDaysAgoConsumption, 4, RoundingMode.HALF_UP)
                        .abs();

                if (fluctuation.compareTo(new BigDecimal("0.5")) > 0) {
                    String typeName = type == Meter.MeterType.ELECTRICITY ? "用电" : "用水";
                    Alert.AlertType alertType = Alert.AlertType.ABNORMAL_FLUCTUATION;

                    if (!alertService.getActiveAlerts().stream()
                            .anyMatch(a -> enterprise.getId().equals(a.getEnterpriseId())
                                    && alertType == a.getType())) {
                        alertService.createAlert(
                                enterprise.getId(),
                                alertType,
                                Alert.AlertLevel.CRITICAL,
                                "重点企业" + typeName + "量异常波动",
                                String.format("%s昨日%s量较前日波动超过50%%，请检查设备运行情况。",
                                        enterprise.getName(), typeName)
                        );

                        if (!inspectionTaskService.getTasksByEnterprise(enterprise.getId()).stream()
                                .anyMatch(t -> t.getStatus() != null
                                        && !t.getStatus().name().equals("COMPLETED")
                                        && !t.getStatus().name().equals("CANCELLED"))) {
                            inspectionTaskService.createTask(
                                    enterprise.getId(),
                                    null,
                                    "用量异常巡检",
                                    "检查" + enterprise.getName() + "的" + typeName + "设备运行情况",
                                    InspectionTask.InspectionPriority.HIGH
                            );
                        }
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 1 1 * ?")
    public void monthlyProcessing() {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();

        int previousMonth = month == 1 ? 12 : month - 1;
        int previousYear = month == 1 ? year - 1 : year;

        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            usageStatisticsService.aggregateMonthlyStatistics(
                    enterprise.getId(), Meter.MeterType.ELECTRICITY, previousYear, previousMonth);
            usageStatisticsService.aggregateMonthlyStatistics(
                    enterprise.getId(), Meter.MeterType.WATER, previousYear, previousMonth);

            billService.generateMonthlyBill(enterprise.getId(), previousYear, previousMonth);
        }

        energyAssessmentService.assessAllEnterprises(previousYear, previousMonth);
        parkRankingService.generateMonthlyRankings(previousYear, previousMonth);
        checkMonthlyQuotaAndCreateAlerts(previousYear, previousMonth);
    }

    private void checkMonthlyQuotaAndCreateAlerts(Integer year, Integer month) {
        List<Enterprise> enterprises = enterpriseRepository.findAll();
        for (Enterprise enterprise : enterprises) {
            List<UsageStatistics> stats = usageStatisticsRepository
                    .findByEnterpriseIdAndPeriod(enterprise.getId(), UsageStatistics.StatisticsPeriod.MONTHLY).stream()
                    .filter(s -> year.equals(s.getYear()) && month.equals(s.getMonth()))
                    .collect(Collectors.toList());

            for (UsageStatistics stat : stats) {
                if (stat.getQuotaPercentage() != null && stat.getQuotaPercentage().compareTo(new BigDecimal("100")) > 0) {
                    String type = stat.getType() == Meter.MeterType.ELECTRICITY ? "用电" : "用水";
                    alertService.createAlert(
                            enterprise.getId(),
                            Alert.AlertType.MONTHLY_QUOTA_EXCEED,
                            Alert.AlertLevel.CRITICAL,
                            type + "月配额超额",
                            String.format("%s月用量已超过配额的%.2f%%，将按阶梯价格收取超额费用。",
                                    type, stat.getQuotaPercentage())
                    );
                }
            }
        }
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void checkOverdueBills() {
        billService.markOverdueBills();
    }

    @Scheduled(cron = "0 */10 * * * ?")
    public void checkMissingDataAndCreateResampleTasks() {
        List<Meter> activeMeters = meterRepository.findActiveMeters();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenMinutesAgo = now.minusMinutes(10);

        for (Meter meter : activeMeters) {
            if (meter.getLastReadingTime() == null || meter.getLastReadingTime().isBefore(tenMinutesAgo)) {
                List<ResampleTask> existing = resampleTaskRepository.findByMeterId(meter.getId()).stream()
                        .filter(t -> ResampleTask.ResampleStatus.PENDING == t.getStatus())
                        .collect(Collectors.toList());

                if (existing.isEmpty()) {
                    LocalDateTime missingFrom = meter.getLastReadingTime() != null
                            ? meter.getLastReadingTime()
                            : tenMinutesAgo.minusHours(1);
                    resampleTaskRepository.save(ResampleTask.builder()
                            .meterId(meter.getId())
                            .enterpriseId(meter.getEnterpriseId())
                            .missingFrom(missingFrom)
                            .missingTo(now)
                            .status(ResampleTask.ResampleStatus.PENDING)
                            .build());

                    alertService.createAlert(
                            meter.getEnterpriseId(),
                            Alert.AlertType.DATA_MISSING,
                            Alert.AlertLevel.WARNING,
                            "表计数据缺失",
                            String.format("表计[%s]数据缺失超过10分钟，已生成补采任务。", meter.getName())
                    );
                }
            }
        }
    }
}
