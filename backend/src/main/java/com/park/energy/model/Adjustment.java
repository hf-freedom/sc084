package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Adjustment implements Serializable {
    private String id;
    private String billId;
    private String enterpriseId;
    private AdjustmentType type;
    private BigDecimal amount;
    private String reason;
    private String createdBy;
    private LocalDateTime createdAt;

    public enum AdjustmentType {
        MANUAL_ADD,
        MANUAL_DEDUCT,
        ENERGY_SAVING_REWARD,
        ERROR_CORRECTION
    }

    public Adjustment() {
    }

    public Adjustment(String id, String billId, String enterpriseId, AdjustmentType type,
                      BigDecimal amount, String reason, String createdBy, LocalDateTime createdAt) {
        this.id = id;
        this.billId = billId;
        this.enterpriseId = enterpriseId;
        this.type = type;
        this.amount = amount;
        this.reason = reason;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public AdjustmentType getType() {
        return type;
    }

    public void setType(AdjustmentType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AdjustmentBuilder builder() {
        return new AdjustmentBuilder();
    }

    public static class AdjustmentBuilder {
        private String id;
        private String billId;
        private String enterpriseId;
        private AdjustmentType type;
        private BigDecimal amount;
        private String reason;
        private String createdBy;
        private LocalDateTime createdAt;

        public AdjustmentBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AdjustmentBuilder billId(String billId) {
            this.billId = billId;
            return this;
        }

        public AdjustmentBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public AdjustmentBuilder type(AdjustmentType type) {
            this.type = type;
            return this;
        }

        public AdjustmentBuilder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public AdjustmentBuilder reason(String reason) {
            this.reason = reason;
            return this;
        }

        public AdjustmentBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public AdjustmentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Adjustment build() {
            return new Adjustment(id, billId, enterpriseId, type, amount, reason, createdBy, createdAt);
        }
    }
}
