package com.park.energy.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Alert implements Serializable {
    private String id;
    private String enterpriseId;
    private AlertType type;
    private AlertLevel level;
    private String title;
    private String description;
    private AlertStatus status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
    private LocalDateTime createdAt;

    public enum AlertType {
        DAILY_QUOTA_WARNING,
        MONTHLY_QUOTA_EXCEED,
        ABNORMAL_FLUCTUATION,
        DATA_MISSING,
        OVERDUE_BILL
    }

    public enum AlertLevel {
        INFO, WARNING, CRITICAL
    }

    public enum AlertStatus {
        ACTIVE, RESOLVED, IGNORED
    }

    public Alert() {
    }

    public Alert(String id, String enterpriseId, AlertType type, AlertLevel level, String title,
                 String description, AlertStatus status, LocalDateTime triggeredAt,
                 LocalDateTime resolvedAt, String resolvedBy, LocalDateTime createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.type = type;
        this.level = level;
        this.title = title;
        this.description = description;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.resolvedBy = resolvedBy;
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

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public AlertLevel getLevel() {
        return level;
    }

    public void setLevel(AlertLevel level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    public void setTriggeredAt(LocalDateTime triggeredAt) {
        this.triggeredAt = triggeredAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(String resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static AlertBuilder builder() {
        return new AlertBuilder();
    }

    public static class AlertBuilder {
        private String id;
        private String enterpriseId;
        private AlertType type;
        private AlertLevel level;
        private String title;
        private String description;
        private AlertStatus status;
        private LocalDateTime triggeredAt;
        private LocalDateTime resolvedAt;
        private String resolvedBy;
        private LocalDateTime createdAt;

        public AlertBuilder id(String id) {
            this.id = id;
            return this;
        }

        public AlertBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public AlertBuilder type(AlertType type) {
            this.type = type;
            return this;
        }

        public AlertBuilder level(AlertLevel level) {
            this.level = level;
            return this;
        }

        public AlertBuilder title(String title) {
            this.title = title;
            return this;
        }

        public AlertBuilder description(String description) {
            this.description = description;
            return this;
        }

        public AlertBuilder status(AlertStatus status) {
            this.status = status;
            return this;
        }

        public AlertBuilder triggeredAt(LocalDateTime triggeredAt) {
            this.triggeredAt = triggeredAt;
            return this;
        }

        public AlertBuilder resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public AlertBuilder resolvedBy(String resolvedBy) {
            this.resolvedBy = resolvedBy;
            return this;
        }

        public AlertBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Alert build() {
            return new Alert(id, enterpriseId, type, level, title, description, status,
                    triggeredAt, resolvedAt, resolvedBy, createdAt);
        }
    }
}
