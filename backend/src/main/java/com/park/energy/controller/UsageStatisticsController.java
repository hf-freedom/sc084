package com.park.energy.controller;

import com.park.energy.model.UsageStatistics;
import com.park.energy.service.UsageStatisticsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class UsageStatisticsController {

    private final UsageStatisticsService usageStatisticsService;

    public UsageStatisticsController(UsageStatisticsService usageStatisticsService) {
        this.usageStatisticsService = usageStatisticsService;
    }

    @GetMapping
    public List<UsageStatistics> getAllStatistics() {
        return usageStatisticsService.getAllStatistics();
    }

    @GetMapping("/enterprise/{enterpriseId}/daily")
    public List<UsageStatistics> getDailyStatisticsByEnterprise(@PathVariable String enterpriseId) {
        return usageStatisticsService.getDailyStatisticsByEnterprise(enterpriseId);
    }

    @GetMapping("/enterprise/{enterpriseId}/monthly")
    public List<UsageStatistics> getMonthlyStatisticsByEnterprise(@PathVariable String enterpriseId) {
        return usageStatisticsService.getMonthlyStatisticsByEnterprise(enterpriseId);
    }

    @GetMapping("/month/{year}/{month}")
    public List<UsageStatistics> getStatisticsByMonth(@PathVariable Integer year, @PathVariable Integer month) {
        return usageStatisticsService.getStatisticsByYearMonth(year, month);
    }
}
