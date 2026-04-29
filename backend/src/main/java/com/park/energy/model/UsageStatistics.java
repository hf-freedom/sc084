package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class UsageStatistics implements Serializable {
    private String id;
    private String enterpriseId;
    private Meter.MeterType type;
    private StatisticsPeriod period;
    private LocalDate periodDate;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private BigDecimal consumption;
    private BigDecimal quota;
    private BigDecimal quotaPercentage;
    private LocalDate createdAt;

    public enum StatisticsPeriod {
        HOURLY, DAILY, MONTHLY
    }

    public UsageStatistics() {
    }

    public UsageStatistics(String id, String enterpriseId, Meter.MeterType type, StatisticsPeriod period,
                           LocalDate periodDate, Integer year, Integer month, Integer day, Integer hour,
                           BigDecimal consumption, BigDecimal quota, BigDecimal quotaPercentage, LocalDate createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.type = type;
        this.period = period;
        this.periodDate = periodDate;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.consumption = consumption;
        this.quota = quota;
        this.quotaPercentage = quotaPercentage;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public Meter.MeterType getType() {
        return type;
    }

    public void setType(Meter.MeterType type) {
        this.type = type;
    }

    public StatisticsPeriod getPeriod() {
        return period;
    }

    public void setPeriod(StatisticsPeriod period) {
        this.period = period;
    }

    public LocalDate getPeriodDate() {
        return periodDate;
    }

    public void setPeriodDate(LocalDate periodDate) {
        this.periodDate = periodDate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    public BigDecimal getQuota() {
        return quota;
    }

    public void setQuota(BigDecimal quota) {
        this.quota = quota;
    }

    public BigDecimal getQuotaPercentage() {
        return quotaPercentage;
    }

    public void setQuotaPercentage(BigDecimal quotaPercentage) {
        this.quotaPercentage = quotaPercentage;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public static UsageStatisticsBuilder builder() {
        return new UsageStatisticsBuilder();
    }

    public static class UsageStatisticsBuilder {
        private String id;
        private String enterpriseId;
        private Meter.MeterType type;
        private StatisticsPeriod period;
        private LocalDate periodDate;
        private Integer year;
        private Integer month;
        private Integer day;
        private Integer hour;
        private BigDecimal consumption;
        private BigDecimal quota;
        private BigDecimal quotaPercentage;
        private LocalDate createdAt;

        public UsageStatisticsBuilder id(String id) {
            this.id = id;
            return this;
        }

        public UsageStatisticsBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public UsageStatisticsBuilder type(Meter.MeterType type) {
            this.type = type;
            return this;
        }

        public UsageStatisticsBuilder period(StatisticsPeriod period) {
            this.period = period;
            return this;
        }

        public UsageStatisticsBuilder periodDate(LocalDate periodDate) {
            this.periodDate = periodDate;
            return this;
        }

        public UsageStatisticsBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public UsageStatisticsBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public UsageStatisticsBuilder day(Integer day) {
            this.day = day;
            return this;
        }

        public UsageStatisticsBuilder hour(Integer hour) {
            this.hour = hour;
            return this;
        }

        public UsageStatisticsBuilder consumption(BigDecimal consumption) {
            this.consumption = consumption;
            return this;
        }

        public UsageStatisticsBuilder quota(BigDecimal quota) {
            this.quota = quota;
            return this;
        }

        public UsageStatisticsBuilder quotaPercentage(BigDecimal quotaPercentage) {
            this.quotaPercentage = quotaPercentage;
            return this;
        }

        public UsageStatisticsBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UsageStatistics build() {
            return new UsageStatistics(id, enterpriseId, type, period, periodDate, year, month, day, hour,
                    consumption, quota, quotaPercentage, createdAt);
        }
    }
}
