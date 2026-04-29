package com.park.energy.service;

import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.model.MeterReading;
import com.park.energy.model.UsageStatistics;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.MeterReadingRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsageStatisticsService {

    private final UsageStatisticsRepository usageStatisticsRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final EnterpriseRepository enterpriseRepository;

    public UsageStatisticsService(UsageStatisticsRepository usageStatisticsRepository,
                                   MeterReadingRepository meterReadingRepository,
                                   EnterpriseRepository enterpriseRepository) {
        this.usageStatisticsRepository = usageStatisticsRepository;
        this.meterReadingRepository = meterReadingRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public void aggregateHourlyStatistics(String enterpriseId, Meter.MeterType type, LocalDateTime hour) {
        LocalDate date = hour.toLocalDate();
        int hourOfDay = hour.getHour();

        LocalDateTime start = hour.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(1);

        BigDecimal consumption = calculateConsumption(enterpriseId, type, start, end);

        Optional<UsageStatistics> existing = usageStatisticsRepository
                .findOneByEnterpriseIdAndPeriodAndDate(enterpriseId, UsageStatistics.StatisticsPeriod.HOURLY, date, type);

        UsageStatistics stat;
        if (existing.isPresent()) {
            stat = existing.get();
            stat.setConsumption(consumption);
        } else {
            stat = UsageStatistics.builder()
                    .enterpriseId(enterpriseId)
                    .type(type)
                    .period(UsageStatistics.StatisticsPeriod.HOURLY)
                    .periodDate(date)
                    .year(date.getYear())
                    .month(date.getMonthValue())
                    .day(date.getDayOfMonth())
                    .hour(hourOfDay)
                    .consumption(consumption)
                    .quota(getQuota(enterpriseId, type, UsageStatistics.StatisticsPeriod.HOURLY))
                    .build();
        }

        updateQuotaPercentage(stat);
        usageStatisticsRepository.save(stat);
    }

    public void aggregateDailyStatistics(String enterpriseId, Meter.MeterType type, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        BigDecimal consumption = calculateConsumption(enterpriseId, type, start, end);

        Optional<UsageStatistics> existing = usageStatisticsRepository
                .findOneByEnterpriseIdAndPeriodAndDate(enterpriseId, UsageStatistics.StatisticsPeriod.DAILY, date, type);

        UsageStatistics stat;
        if (existing.isPresent()) {
            stat = existing.get();
            stat.setConsumption(consumption);
        } else {
            stat = UsageStatistics.builder()
                    .enterpriseId(enterpriseId)
                    .type(type)
                    .period(UsageStatistics.StatisticsPeriod.DAILY)
                    .periodDate(date)
                    .year(date.getYear())
                    .month(date.getMonthValue())
                    .day(date.getDayOfMonth())
                    .consumption(consumption)
                    .quota(getQuota(enterpriseId, type, UsageStatistics.StatisticsPeriod.DAILY))
                    .build();
        }

        updateQuotaPercentage(stat);
        usageStatisticsRepository.save(stat);
    }

    public void aggregateMonthlyStatistics(String enterpriseId, Meter.MeterType type, Integer year, Integer month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.plusMonths(1).minusDays(1);

        LocalDateTime start = firstDay.atStartOfDay();
        LocalDateTime end = lastDay.plusDays(1).atStartOfDay();

        BigDecimal consumption = calculateConsumption(enterpriseId, type, start, end);

        Optional<UsageStatistics> existing = usageStatisticsRepository
                .findOneByEnterpriseIdAndPeriodAndDate(enterpriseId, UsageStatistics.StatisticsPeriod.MONTHLY, firstDay, type);

        UsageStatistics stat;
        if (existing.isPresent()) {
            stat = existing.get();
            stat.setConsumption(consumption);
        } else {
            stat = UsageStatistics.builder()
                    .enterpriseId(enterpriseId)
                    .type(type)
                    .period(UsageStatistics.StatisticsPeriod.MONTHLY)
                    .periodDate(firstDay)
                    .year(year)
                    .month(month)
                    .consumption(consumption)
                    .quota(getQuota(enterpriseId, type, UsageStatistics.StatisticsPeriod.MONTHLY))
                    .build();
        }

        updateQuotaPercentage(stat);
        usageStatisticsRepository.save(stat);
    }

    private BigDecimal calculateConsumption(String enterpriseId, Meter.MeterType type, LocalDateTime start, LocalDateTime end) {
        List<MeterReading> readings = meterReadingRepository
                .findByEnterpriseIdAndType(enterpriseId, type).stream()
                .filter(r -> !r.getReadingTime().isBefore(start) && r.getReadingTime().isBefore(end))
                .collect(Collectors.toList());

        return readings.stream()
                .map(MeterReading::getConsumption)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getQuota(String enterpriseId, Meter.MeterType type, UsageStatistics.StatisticsPeriod period) {
        Optional<Enterprise> enterpriseOpt = enterpriseRepository.findById(enterpriseId);
        if (!enterpriseOpt.isPresent()) {
            return BigDecimal.ZERO;
        }

        Enterprise enterprise = enterpriseOpt.get();
        if (type == Meter.MeterType.ELECTRICITY) {
            if (period == UsageStatistics.StatisticsPeriod.MONTHLY) {
                return enterprise.getMonthlyElectricityQuota();
            } else {
                return enterprise.getDailyElectricityQuota();
            }
        } else {
            if (period == UsageStatistics.StatisticsPeriod.MONTHLY) {
                return enterprise.getMonthlyWaterQuota();
            } else {
                return enterprise.getDailyWaterQuota();
            }
        }
    }

    private void updateQuotaPercentage(UsageStatistics stat) {
        if (stat.getQuota() != null && stat.getQuota().compareTo(BigDecimal.ZERO) > 0) {
            stat.setQuotaPercentage(stat.getConsumption()
                    .divide(stat.getQuota(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")));
        } else {
            stat.setQuotaPercentage(BigDecimal.ZERO);
        }
    }

    public List<UsageStatistics> getDailyStatisticsByEnterprise(String enterpriseId) {
        return usageStatisticsRepository.findByEnterpriseIdAndPeriod(enterpriseId, UsageStatistics.StatisticsPeriod.DAILY);
    }

    public List<UsageStatistics> getMonthlyStatisticsByEnterprise(String enterpriseId) {
        return usageStatisticsRepository.findByEnterpriseIdAndPeriod(enterpriseId, UsageStatistics.StatisticsPeriod.MONTHLY);
    }

    public List<UsageStatistics> getStatisticsByYearMonth(Integer year, Integer month) {
        return usageStatisticsRepository.findByYearMonth(year, month);
    }

    public List<UsageStatistics> getAllStatistics() {
        return usageStatisticsRepository.findAll();
    }
}
