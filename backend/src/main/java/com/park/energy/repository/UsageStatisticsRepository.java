package com.park.energy.repository;

import com.park.energy.model.Meter;
import com.park.energy.model.UsageStatistics;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class UsageStatisticsRepository {

    private final Map<String, UsageStatistics> statistics = new ConcurrentHashMap<>();

    public UsageStatistics save(UsageStatistics stat) {
        if (stat.getId() == null) {
            stat.setId(UUID.randomUUID().toString());
        }
        if (stat.getCreatedAt() == null) {
            stat.setCreatedAt(LocalDate.now());
        }
        statistics.put(stat.getId(), stat);
        return stat;
    }

    public Optional<UsageStatistics> findById(String id) {
        return Optional.ofNullable(statistics.get(id));
    }

    public List<UsageStatistics> findAll() {
        return new ArrayList<>(statistics.values());
    }

    public List<UsageStatistics> findByEnterpriseId(String enterpriseId) {
        return statistics.values().stream()
                .filter(s -> enterpriseId.equals(s.getEnterpriseId()))
                .sorted(Comparator.comparing(UsageStatistics::getPeriodDate).reversed())
                .collect(Collectors.toList());
    }

    public List<UsageStatistics> findByEnterpriseIdAndPeriod(String enterpriseId, UsageStatistics.StatisticsPeriod period) {
        return statistics.values().stream()
                .filter(s -> enterpriseId.equals(s.getEnterpriseId()) && period == s.getPeriod())
                .sorted(Comparator.comparing(UsageStatistics::getPeriodDate).reversed())
                .collect(Collectors.toList());
    }

    public List<UsageStatistics> findByEnterpriseIdAndPeriodAndDate(
            String enterpriseId, UsageStatistics.StatisticsPeriod period, LocalDate date) {
        return statistics.values().stream()
                .filter(s -> enterpriseId.equals(s.getEnterpriseId())
                        && period == s.getPeriod()
                        && date.equals(s.getPeriodDate()))
                .collect(Collectors.toList());
    }

    public Optional<UsageStatistics> findOneByEnterpriseIdAndPeriodAndDate(
            String enterpriseId, UsageStatistics.StatisticsPeriod period, LocalDate date, Meter.MeterType type) {
        return statistics.values().stream()
                .filter(s -> enterpriseId.equals(s.getEnterpriseId())
                        && period == s.getPeriod()
                        && date.equals(s.getPeriodDate())
                        && type == s.getType())
                .findFirst();
    }

    public List<UsageStatistics> findByYearMonth(Integer year, Integer month) {
        return statistics.values().stream()
                .filter(s -> year.equals(s.getYear()) && month.equals(s.getMonth()))
                .collect(Collectors.toList());
    }

    public List<UsageStatistics> findByEnterpriseIdAndYearMonth(String enterpriseId, Integer year, Integer month) {
        return statistics.values().stream()
                .filter(s -> enterpriseId.equals(s.getEnterpriseId())
                        && year.equals(s.getYear())
                        && month.equals(s.getMonth()))
                .sorted(Comparator.comparing(UsageStatistics::getPeriodDate))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        statistics.remove(id);
    }
}
