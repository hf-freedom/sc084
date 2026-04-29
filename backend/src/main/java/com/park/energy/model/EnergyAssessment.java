package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class EnergyAssessment implements Serializable {
    private String id;
    private String enterpriseId;
    private Integer year;
    private Integer month;
    private BigDecimal electricityConsumption;
    private BigDecimal waterConsumption;
    private BigDecimal electricityQuota;
    private BigDecimal waterQuota;
    private BigDecimal electricitySavingRate;
    private BigDecimal waterSavingRate;
    private BigDecimal overallSavingRate;
    private AssessmentLevel level;
    private Boolean eligibleForDiscount;
    private LocalDate assessedAt;
    private LocalDate createdAt;

    public enum AssessmentLevel {
        EXCELLENT, GOOD, PASS, FAIL
    }

    public EnergyAssessment() {
    }

    public EnergyAssessment(String id, String enterpriseId, Integer year, Integer month,
                            BigDecimal electricityConsumption, BigDecimal waterConsumption,
                            BigDecimal electricityQuota, BigDecimal waterQuota,
                            BigDecimal electricitySavingRate, BigDecimal waterSavingRate,
                            BigDecimal overallSavingRate, AssessmentLevel level,
                            Boolean eligibleForDiscount, LocalDate assessedAt, LocalDate createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.year = year;
        this.month = month;
        this.electricityConsumption = electricityConsumption;
        this.waterConsumption = waterConsumption;
        this.electricityQuota = electricityQuota;
        this.waterQuota = waterQuota;
        this.electricitySavingRate = electricitySavingRate;
        this.waterSavingRate = waterSavingRate;
        this.overallSavingRate = overallSavingRate;
        this.level = level;
        this.eligibleForDiscount = eligibleForDiscount;
        this.assessedAt = assessedAt;
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

    public BigDecimal getElectricityConsumption() {
        return electricityConsumption;
    }

    public void setElectricityConsumption(BigDecimal electricityConsumption) {
        this.electricityConsumption = electricityConsumption;
    }

    public BigDecimal getWaterConsumption() {
        return waterConsumption;
    }

    public void setWaterConsumption(BigDecimal waterConsumption) {
        this.waterConsumption = waterConsumption;
    }

    public BigDecimal getElectricityQuota() {
        return electricityQuota;
    }

    public void setElectricityQuota(BigDecimal electricityQuota) {
        this.electricityQuota = electricityQuota;
    }

    public BigDecimal getWaterQuota() {
        return waterQuota;
    }

    public void setWaterQuota(BigDecimal waterQuota) {
        this.waterQuota = waterQuota;
    }

    public BigDecimal getElectricitySavingRate() {
        return electricitySavingRate;
    }

    public void setElectricitySavingRate(BigDecimal electricitySavingRate) {
        this.electricitySavingRate = electricitySavingRate;
    }

    public BigDecimal getWaterSavingRate() {
        return waterSavingRate;
    }

    public void setWaterSavingRate(BigDecimal waterSavingRate) {
        this.waterSavingRate = waterSavingRate;
    }

    public BigDecimal getOverallSavingRate() {
        return overallSavingRate;
    }

    public void setOverallSavingRate(BigDecimal overallSavingRate) {
        this.overallSavingRate = overallSavingRate;
    }

    public AssessmentLevel getLevel() {
        return level;
    }

    public void setLevel(AssessmentLevel level) {
        this.level = level;
    }

    public Boolean getEligibleForDiscount() {
        return eligibleForDiscount;
    }

    public void setEligibleForDiscount(Boolean eligibleForDiscount) {
        this.eligibleForDiscount = eligibleForDiscount;
    }

    public Boolean getQualified() {
        return eligibleForDiscount;
    }

    public LocalDate getAssessedAt() {
        return assessedAt;
    }

    public void setAssessedAt(LocalDate assessedAt) {
        this.assessedAt = assessedAt;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public static EnergyAssessmentBuilder builder() {
        return new EnergyAssessmentBuilder();
    }

    public static class EnergyAssessmentBuilder {
        private String id;
        private String enterpriseId;
        private Integer year;
        private Integer month;
        private BigDecimal electricityConsumption;
        private BigDecimal waterConsumption;
        private BigDecimal electricityQuota;
        private BigDecimal waterQuota;
        private BigDecimal electricitySavingRate;
        private BigDecimal waterSavingRate;
        private BigDecimal overallSavingRate;
        private AssessmentLevel level;
        private Boolean eligibleForDiscount;
        private LocalDate assessedAt;
        private LocalDate createdAt;

        public EnergyAssessmentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EnergyAssessmentBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public EnergyAssessmentBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public EnergyAssessmentBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public EnergyAssessmentBuilder electricityConsumption(BigDecimal electricityConsumption) {
            this.electricityConsumption = electricityConsumption;
            return this;
        }

        public EnergyAssessmentBuilder waterConsumption(BigDecimal waterConsumption) {
            this.waterConsumption = waterConsumption;
            return this;
        }

        public EnergyAssessmentBuilder electricityQuota(BigDecimal electricityQuota) {
            this.electricityQuota = electricityQuota;
            return this;
        }

        public EnergyAssessmentBuilder waterQuota(BigDecimal waterQuota) {
            this.waterQuota = waterQuota;
            return this;
        }

        public EnergyAssessmentBuilder electricitySavingRate(BigDecimal electricitySavingRate) {
            this.electricitySavingRate = electricitySavingRate;
            return this;
        }

        public EnergyAssessmentBuilder waterSavingRate(BigDecimal waterSavingRate) {
            this.waterSavingRate = waterSavingRate;
            return this;
        }

        public EnergyAssessmentBuilder overallSavingRate(BigDecimal overallSavingRate) {
            this.overallSavingRate = overallSavingRate;
            return this;
        }

        public EnergyAssessmentBuilder level(AssessmentLevel level) {
            this.level = level;
            return this;
        }

        public EnergyAssessmentBuilder eligibleForDiscount(Boolean eligibleForDiscount) {
            this.eligibleForDiscount = eligibleForDiscount;
            return this;
        }

        public EnergyAssessmentBuilder assessedAt(LocalDate assessedAt) {
            this.assessedAt = assessedAt;
            return this;
        }

        public EnergyAssessmentBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EnergyAssessment build() {
            return new EnergyAssessment(id, enterpriseId, year, month, electricityConsumption, waterConsumption,
                    electricityQuota, waterQuota, electricitySavingRate, waterSavingRate, overallSavingRate,
                    level, eligibleForDiscount, assessedAt, createdAt);
        }
    }
}
