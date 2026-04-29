package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Enterprise implements Serializable {
    private String id;
    private String name;
    private String industryType;
    private BigDecimal monthlyElectricityQuota;
    private BigDecimal monthlyWaterQuota;
    private BigDecimal dailyElectricityQuota;
    private BigDecimal dailyWaterQuota;
    private Boolean keyEnterprise;
    private Boolean servicesRestricted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Enterprise() {
    }

    public Enterprise(String id, String name, String industryType, BigDecimal monthlyElectricityQuota,
                      BigDecimal monthlyWaterQuota, BigDecimal dailyElectricityQuota, BigDecimal dailyWaterQuota,
                      Boolean keyEnterprise, Boolean servicesRestricted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.industryType = industryType;
        this.monthlyElectricityQuota = monthlyElectricityQuota;
        this.monthlyWaterQuota = monthlyWaterQuota;
        this.dailyElectricityQuota = dailyElectricityQuota;
        this.dailyWaterQuota = dailyWaterQuota;
        this.keyEnterprise = keyEnterprise;
        this.servicesRestricted = servicesRestricted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public BigDecimal getMonthlyElectricityQuota() {
        return monthlyElectricityQuota;
    }

    public void setMonthlyElectricityQuota(BigDecimal monthlyElectricityQuota) {
        this.monthlyElectricityQuota = monthlyElectricityQuota;
    }

    public BigDecimal getMonthlyWaterQuota() {
        return monthlyWaterQuota;
    }

    public void setMonthlyWaterQuota(BigDecimal monthlyWaterQuota) {
        this.monthlyWaterQuota = monthlyWaterQuota;
    }

    public BigDecimal getDailyElectricityQuota() {
        return dailyElectricityQuota;
    }

    public void setDailyElectricityQuota(BigDecimal dailyElectricityQuota) {
        this.dailyElectricityQuota = dailyElectricityQuota;
    }

    public BigDecimal getDailyWaterQuota() {
        return dailyWaterQuota;
    }

    public void setDailyWaterQuota(BigDecimal dailyWaterQuota) {
        this.dailyWaterQuota = dailyWaterQuota;
    }

    public Boolean getKeyEnterprise() {
        return keyEnterprise;
    }

    public void setKeyEnterprise(Boolean keyEnterprise) {
        this.keyEnterprise = keyEnterprise;
    }

    public Boolean getServicesRestricted() {
        return servicesRestricted;
    }

    public void setServicesRestricted(Boolean servicesRestricted) {
        this.servicesRestricted = servicesRestricted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static EnterpriseBuilder builder() {
        return new EnterpriseBuilder();
    }

    public static class EnterpriseBuilder {
        private String id;
        private String name;
        private String industryType;
        private BigDecimal monthlyElectricityQuota;
        private BigDecimal monthlyWaterQuota;
        private BigDecimal dailyElectricityQuota;
        private BigDecimal dailyWaterQuota;
        private Boolean keyEnterprise;
        private Boolean servicesRestricted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public EnterpriseBuilder id(String id) {
            this.id = id;
            return this;
        }

        public EnterpriseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EnterpriseBuilder industryType(String industryType) {
            this.industryType = industryType;
            return this;
        }

        public EnterpriseBuilder monthlyElectricityQuota(BigDecimal monthlyElectricityQuota) {
            this.monthlyElectricityQuota = monthlyElectricityQuota;
            return this;
        }

        public EnterpriseBuilder monthlyWaterQuota(BigDecimal monthlyWaterQuota) {
            this.monthlyWaterQuota = monthlyWaterQuota;
            return this;
        }

        public EnterpriseBuilder dailyElectricityQuota(BigDecimal dailyElectricityQuota) {
            this.dailyElectricityQuota = dailyElectricityQuota;
            return this;
        }

        public EnterpriseBuilder dailyWaterQuota(BigDecimal dailyWaterQuota) {
            this.dailyWaterQuota = dailyWaterQuota;
            return this;
        }

        public EnterpriseBuilder keyEnterprise(Boolean keyEnterprise) {
            this.keyEnterprise = keyEnterprise;
            return this;
        }

        public EnterpriseBuilder servicesRestricted(Boolean servicesRestricted) {
            this.servicesRestricted = servicesRestricted;
            return this;
        }

        public EnterpriseBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public EnterpriseBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Enterprise build() {
            return new Enterprise(id, name, industryType, monthlyElectricityQuota, monthlyWaterQuota,
                    dailyElectricityQuota, dailyWaterQuota, keyEnterprise, servicesRestricted, createdAt, updatedAt);
        }
    }
}
