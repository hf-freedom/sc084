package com.park.energy.controller;

import com.park.energy.model.ParkRanking;
import com.park.energy.service.ParkRankingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@CrossOrigin(origins = "*")
public class ParkRankingController {

    private final ParkRankingService parkRankingService;

    public ParkRankingController(ParkRankingService parkRankingService) {
        this.parkRankingService = parkRankingService;
    }

    @GetMapping
    public List<ParkRanking> getAllRankings() {
        return parkRankingService.getAllRankings();
    }

    @PostMapping("/generate/{year}/{month}")
    public void generateRankings(@PathVariable Integer year, @PathVariable Integer month) {
        parkRankingService.generateMonthlyRankings(year, month);
    }

    @GetMapping("/month/{year}/{month}")
    public List<ParkRanking> getRankingsByMonth(@PathVariable Integer year, @PathVariable Integer month) {
        return parkRankingService.getRankingsByYearMonth(year, month);
    }

    @GetMapping("/month/{year}/{month}/type/{type}")
    public List<ParkRanking> getRankingsByMonthAndType(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @PathVariable ParkRanking.RankingType type) {
        return parkRankingService.getRankingsByYearMonthAndType(year, month, type);
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<ParkRanking> getRankingsByEnterprise(@PathVariable String enterpriseId) {
        return parkRankingService.getRankingsByEnterprise(enterpriseId);
    }
}
