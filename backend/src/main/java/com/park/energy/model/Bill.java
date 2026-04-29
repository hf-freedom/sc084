package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable {
    private String id;
    private String enterpriseId;
    private Integer year;
    private Integer month;
    private BigDecimal electricityConsumption;
    private BigDecimal waterConsumption;
    private BigDecimal electricityQuota;
    private BigDecimal waterQuota;
    private BigDecimal electricityOverQuota;
    private BigDecimal waterOverQuota;
    private BigDecimal basicElectricityCost;
    private BigDecimal basicWaterCost;
    private BigDecimal overQuotaElectricityCost;
    private BigDecimal overQuotaWaterCost;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private BigDecimal paidAmount;
    private BillStatus status;
    private List<Adjustment> adjustments = new ArrayList<>();
    private LocalDateTime generatedAt;
    private LocalDateTime confirmedAt;
    private String confirmedBy;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;

    public enum BillStatus {
        DRAFT, CONFIRMED, PAID, PARTIALLY_PAID, OVERDUE
    }

    public Bill() {
    }

    public Bill(String id, String enterpriseId, Integer year, Integer month,
                BigDecimal electricityConsumption, BigDecimal waterConsumption,
                BigDecimal electricityQuota, BigDecimal waterQuota,
                BigDecimal electricityOverQuota, BigDecimal waterOverQuota,
                BigDecimal basicElectricityCost, BigDecimal basicWaterCost,
                BigDecimal overQuotaElectricityCost, BigDecimal overQuotaWaterCost,
                BigDecimal totalAmount, BigDecimal discountAmount, BigDecimal finalAmount,
                BigDecimal paidAmount, BillStatus status, List<Adjustment> adjustments,
                LocalDateTime generatedAt, LocalDateTime confirmedAt, String confirmedBy,
                LocalDateTime paidAt, LocalDateTime createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.year = year;
        this.month = month;
        this.electricityConsumption = electricityConsumption;
        this.waterConsumption = waterConsumption;
        this.electricityQuota = electricityQuota;
        this.waterQuota = waterQuota;
        this.electricityOverQuota = electricityOverQuota;
        this.waterOverQuota = waterOverQuota;
        this.basicElectricityCost = basicElectricityCost;
        this.basicWaterCost = basicWaterCost;
        this.overQuotaElectricityCost = overQuotaElectricityCost;
        this.overQuotaWaterCost = overQuotaWaterCost;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paidAmount = paidAmount;
        this.status = status;
        this.adjustments = adjustments != null ? adjustments : new ArrayList<>();
        this.generatedAt = generatedAt;
        this.confirmedAt = confirmedAt;
        this.confirmedBy = confirmedBy;
        this.paidAt = paidAt;
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

    public BigDecimal getElectricityOverQuota() {
        return electricityOverQuota;
    }

    public void setElectricityOverQuota(BigDecimal electricityOverQuota) {
        this.electricityOverQuota = electricityOverQuota;
    }

    public BigDecimal getWaterOverQuota() {
        return waterOverQuota;
    }

    public void setWaterOverQuota(BigDecimal waterOverQuota) {
        this.waterOverQuota = waterOverQuota;
    }

    public BigDecimal getBasicElectricityCost() {
        return basicElectricityCost;
    }

    public void setBasicElectricityCost(BigDecimal basicElectricityCost) {
        this.basicElectricityCost = basicElectricityCost;
    }

    public BigDecimal getBasicWaterCost() {
        return basicWaterCost;
    }

    public void setBasicWaterCost(BigDecimal basicWaterCost) {
        this.basicWaterCost = basicWaterCost;
    }

    public BigDecimal getOverQuotaElectricityCost() {
        return overQuotaElectricityCost;
    }

    public void setOverQuotaElectricityCost(BigDecimal overQuotaElectricityCost) {
        this.overQuotaElectricityCost = overQuotaElectricityCost;
    }

    public BigDecimal getOverQuotaWaterCost() {
        return overQuotaWaterCost;
    }

    public void setOverQuotaWaterCost(BigDecimal overQuotaWaterCost) {
        this.overQuotaWaterCost = overQuotaWaterCost;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<Adjustment> getAdjustments() {
        return adjustments;
    }

    public void setAdjustments(List<Adjustment> adjustments) {
        this.adjustments = adjustments;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static BillBuilder builder() {
        return new BillBuilder();
    }

    public static class BillBuilder {
        private String id;
        private String enterpriseId;
        private Integer year;
        private Integer month;
        private BigDecimal electricityConsumption;
        private BigDecimal waterConsumption;
        private BigDecimal electricityQuota;
        private BigDecimal waterQuota;
        private BigDecimal electricityOverQuota;
        private BigDecimal waterOverQuota;
        private BigDecimal basicElectricityCost;
        private BigDecimal basicWaterCost;
        private BigDecimal overQuotaElectricityCost;
        private BigDecimal overQuotaWaterCost;
        private BigDecimal totalAmount;
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private BigDecimal paidAmount;
        private BillStatus status;
        private List<Adjustment> adjustments = new ArrayList<>();
        private LocalDateTime generatedAt;
        private LocalDateTime confirmedAt;
        private String confirmedBy;
        private LocalDateTime paidAt;
        private LocalDateTime createdAt;

        public BillBuilder id(String id) {
            this.id = id;
            return this;
        }

        public BillBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public BillBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public BillBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public BillBuilder electricityConsumption(BigDecimal electricityConsumption) {
            this.electricityConsumption = electricityConsumption;
            return this;
        }

        public BillBuilder waterConsumption(BigDecimal waterConsumption) {
            this.waterConsumption = waterConsumption;
            return this;
        }

        public BillBuilder electricityQuota(BigDecimal electricityQuota) {
            this.electricityQuota = electricityQuota;
            return this;
        }

        public BillBuilder waterQuota(BigDecimal waterQuota) {
            this.waterQuota = waterQuota;
            return this;
        }

        public BillBuilder electricityOverQuota(BigDecimal electricityOverQuota) {
            this.electricityOverQuota = electricityOverQuota;
            return this;
        }

        public BillBuilder waterOverQuota(BigDecimal waterOverQuota) {
            this.waterOverQuota = waterOverQuota;
            return this;
        }

        public BillBuilder basicElectricityCost(BigDecimal basicElectricityCost) {
            this.basicElectricityCost = basicElectricityCost;
            return this;
        }

        public BillBuilder basicWaterCost(BigDecimal basicWaterCost) {
            this.basicWaterCost = basicWaterCost;
            return this;
        }

        public BillBuilder overQuotaElectricityCost(BigDecimal overQuotaElectricityCost) {
            this.overQuotaElectricityCost = overQuotaElectricityCost;
            return this;
        }

        public BillBuilder overQuotaWaterCost(BigDecimal overQuotaWaterCost) {
            this.overQuotaWaterCost = overQuotaWaterCost;
            return this;
        }

        public BillBuilder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public BillBuilder discountAmount(BigDecimal discountAmount) {
            this.discountAmount = discountAmount;
            return this;
        }

        public BillBuilder finalAmount(BigDecimal finalAmount) {
            this.finalAmount = finalAmount;
            return this;
        }

        public BillBuilder paidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
            return this;
        }

        public BillBuilder status(BillStatus status) {
            this.status = status;
            return this;
        }

        public BillBuilder adjustments(List<Adjustment> adjustments) {
            this.adjustments = adjustments != null ? adjustments : new ArrayList<>();
            return this;
        }

        public BillBuilder generatedAt(LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public BillBuilder confirmedAt(LocalDateTime confirmedAt) {
            this.confirmedAt = confirmedAt;
            return this;
        }

        public BillBuilder confirmedBy(String confirmedBy) {
            this.confirmedBy = confirmedBy;
            return this;
        }

        public BillBuilder paidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
            return this;
        }

        public BillBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Bill build() {
            return new Bill(id, enterpriseId, year, month, electricityConsumption, waterConsumption,
                    electricityQuota, waterQuota, electricityOverQuota, waterOverQuota,
                    basicElectricityCost, basicWaterCost, overQuotaElectricityCost, overQuotaWaterCost,
                    totalAmount, discountAmount, finalAmount, paidAmount, status, adjustments,
                    generatedAt, confirmedAt, confirmedBy, paidAt, createdAt);
        }
    }
}
