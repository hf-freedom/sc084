package com.park.energy.service;

import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.model.ParkRanking;
import com.park.energy.model.UsageStatistics;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.ParkRankingRepository;
import com.park.energy.repository.UsageStatisticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ParkRankingService {

    private final ParkRankingRepository rankingRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final UsageStatisticsRepository usageStatisticsRepository;

    public ParkRankingService(ParkRankingRepository rankingRepository,
                              EnterpriseRepository enterpriseRepository,
                              UsageStatisticsRepository usageStatisticsRepository) {
        this.rankingRepository = rankingRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.usageStatisticsRepository = usageStatisticsRepository;
    }

    public void generateMonthlyRankings(Integer year, Integer month) {
        rankingRepository.deleteByYearMonth(year, month);

        List<Enterprise> enterprises = enterpriseRepository.findAll();

        List<RankingData> electricityRankings = new ArrayList<>();
        List<RankingData> waterRankings = new ArrayList<>();
        List<RankingData> totalRankings = new ArrayList<>();

        for (Enterprise enterprise : enterprises) {
            List<UsageStatistics> stats = usageStatisticsRepository
                    .findByEnterpriseIdAndPeriod(enterprise.getId(), UsageStatistics.StatisticsPeriod.MONTHLY).stream()
                    .filter(s -> year.equals(s.getYear()) && month.equals(s.getMonth()))
                    .collect(Collectors.toList());

            BigDecimal electricityConsumption = stats.stream()
                    .filter(s -> Meter.MeterType.ELECTRICITY == s.getType())
                    .findFirst()
                    .map(UsageStatistics::getConsumption)
                    .orElse(BigDecimal.ZERO);

            BigDecimal waterConsumption = stats.stream()
                    .filter(s -> Meter.MeterType.WATER == s.getType())
                    .findFirst()
                    .map(UsageStatistics::getConsumption)
                    .orElse(BigDecimal.ZERO);

            BigDecimal totalConsumption = electricityConsumption.add(waterConsumption);

            electricityRankings.add(new RankingData(enterprise.getId(), enterprise.getName(), electricityConsumption));
            waterRankings.add(new RankingData(enterprise.getId(), enterprise.getName(), waterConsumption));
            totalRankings.add(new RankingData(enterprise.getId(), enterprise.getName(), totalConsumption));
        }

        saveRankings(electricityRankings, ParkRanking.RankingType.ELECTRICITY_CONSUMPTION, year, month);
        saveRankings(waterRankings, ParkRanking.RankingType.WATER_CONSUMPTION, year, month);
        saveRankings(totalRankings, ParkRanking.RankingType.TOTAL_CONSUMPTION, year, month);
    }

    private void saveRankings(List<RankingData> dataList, ParkRanking.RankingType type, Integer year, Integer month) {
        List<RankingData> sorted = dataList.stream()
                .sorted(Comparator.comparing(RankingData::getValue).reversed())
                .collect(Collectors.toList());

        int rank = 1;
        for (RankingData data : sorted) {
            ParkRanking ranking = ParkRanking.builder()
                    .enterpriseId(data.getEnterpriseId())
                    .enterpriseName(data.getEnterpriseName())
                    .year(year)
                    .month(month)
                    .rank(rank++)
                    .type(type)
                    .value(data.getValue())
                    .build();
            rankingRepository.save(ranking);
        }
    }

    public List<ParkRanking> getRankingsByYearMonth(Integer year, Integer month) {
        return rankingRepository.findByYearMonth(year, month);
    }

    public List<ParkRanking> getRankingsByYearMonthAndType(Integer year, Integer month, ParkRanking.RankingType type) {
        return rankingRepository.findByYearMonthAndType(year, month, type);
    }

    public List<ParkRanking> getRankingsByEnterprise(String enterpriseId) {
        return rankingRepository.findByEnterpriseId(enterpriseId);
    }

    public List<ParkRanking> getAllRankings() {
        return rankingRepository.findAll();
    }

    public Optional<ParkRanking> getRankingById(String id) {
        return rankingRepository.findById(id);
    }

    private static class RankingData {
        private final String enterpriseId;
        private final String enterpriseName;
        private final BigDecimal value;

        public RankingData(String enterpriseId, String enterpriseName, BigDecimal value) {
            this.enterpriseId = enterpriseId;
            this.enterpriseName = enterpriseName;
            this.value = value;
        }

        public String getEnterpriseId() {
            return enterpriseId;
        }

        public String getEnterpriseName() {
            return enterpriseName;
        }

        public BigDecimal getValue() {
            return value;
        }
    }
}
