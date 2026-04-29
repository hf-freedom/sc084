package com.park.energy.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ResampleTask implements Serializable {
    private String id;
    private String meterId;
    private String enterpriseId;
    private LocalDateTime missingFrom;
    private LocalDateTime missingTo;
    private ResampleStatus status;
    private String result;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;

    public enum ResampleStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED, FAILED
    }

    public ResampleTask() {
    }

    public ResampleTask(String id, String meterId, String enterpriseId, LocalDateTime missingFrom,
                        LocalDateTime missingTo, ResampleStatus status, String result,
                        LocalDateTime completedAt, LocalDateTime createdAt) {
        this.id = id;
        this.meterId = meterId;
        this.enterpriseId = enterpriseId;
        this.missingFrom = missingFrom;
        this.missingTo = missingTo;
        this.status = status;
        this.result = result;
        this.completedAt = completedAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public LocalDateTime getMissingFrom() {
        return missingFrom;
    }

    public void setMissingFrom(LocalDateTime missingFrom) {
        this.missingFrom = missingFrom;
    }

    public LocalDateTime getMissingTo() {
        return missingTo;
    }

    public void setMissingTo(LocalDateTime missingTo) {
        this.missingTo = missingTo;
    }

    public ResampleStatus getStatus() {
        return status;
    }

    public void setStatus(ResampleStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static ResampleTaskBuilder builder() {
        return new ResampleTaskBuilder();
    }

    public static class ResampleTaskBuilder {
        private String id;
        private String meterId;
        private String enterpriseId;
        private LocalDateTime missingFrom;
        private LocalDateTime missingTo;
        private ResampleStatus status;
        private String result;
        private LocalDateTime completedAt;
        private LocalDateTime createdAt;

        public ResampleTaskBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ResampleTaskBuilder meterId(String meterId) {
            this.meterId = meterId;
            return this;
        }

        public ResampleTaskBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public ResampleTaskBuilder missingFrom(LocalDateTime missingFrom) {
            this.missingFrom = missingFrom;
            return this;
        }

        public ResampleTaskBuilder missingTo(LocalDateTime missingTo) {
            this.missingTo = missingTo;
            return this;
        }

        public ResampleTaskBuilder status(ResampleStatus status) {
            this.status = status;
            return this;
        }

        public ResampleTaskBuilder result(String result) {
            this.result = result;
            return this;
        }

        public ResampleTaskBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public ResampleTaskBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ResampleTask build() {
            return new ResampleTask(id, meterId, enterpriseId, missingFrom, missingTo, status, result, completedAt, createdAt);
        }
    }
}
