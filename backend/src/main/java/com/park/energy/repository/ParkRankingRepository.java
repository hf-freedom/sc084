package com.park.energy.repository;

import com.park.energy.model.ParkRanking;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ParkRankingRepository {

    private final Map<String, ParkRanking> rankings = new ConcurrentHashMap<>();

    public ParkRanking save(ParkRanking ranking) {
        if (ranking.getId() == null) {
            ranking.setId(UUID.randomUUID().toString());
        }
        if (ranking.getCreatedAt() == null) {
            ranking.setCreatedAt(LocalDate.now());
        }
        rankings.put(ranking.getId(), ranking);
        return ranking;
    }

    public Optional<ParkRanking> findById(String id) {
        return Optional.ofNullable(rankings.get(id));
    }

    public List<ParkRanking> findAll() {
        return new ArrayList<>(rankings.values());
    }

    public List<ParkRanking> findByYearMonth(Integer year, Integer month) {
        return rankings.values().stream()
                .filter(r -> year.equals(r.getYear()) && month.equals(r.getMonth()))
                .sorted(Comparator.comparing(ParkRanking::getRank))
                .collect(Collectors.toList());
    }

    public List<ParkRanking> findByYearMonthAndType(Integer year, Integer month, ParkRanking.RankingType type) {
        return rankings.values().stream()
                .filter(r -> year.equals(r.getYear())
                        && month.equals(r.getMonth())
                        && type == r.getType())
                .sorted(Comparator.comparing(ParkRanking::getRank))
                .collect(Collectors.toList());
    }

    public List<ParkRanking> findByEnterpriseId(String enterpriseId) {
        return rankings.values().stream()
                .filter(r -> enterpriseId.equals(r.getEnterpriseId()))
                .sorted(Comparator.comparing(ParkRanking::getYear).reversed()
                        .thenComparing(ParkRanking::getMonth).reversed())
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        rankings.remove(id);
    }

    public void deleteByYearMonth(Integer year, Integer month) {
        List<String> toDelete = rankings.values().stream()
                .filter(r -> year.equals(r.getYear()) && month.equals(r.getMonth()))
                .map(ParkRanking::getId)
                .collect(Collectors.toList());
        toDelete.forEach(rankings::remove);
    }
}
