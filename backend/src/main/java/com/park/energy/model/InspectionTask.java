package com.park.energy.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class InspectionTask implements Serializable {
    private String id;
    private String enterpriseId;
    private String relatedAlertId;
    private String title;
    private String description;
    private InspectionPriority priority;
    private InspectionStatus status;
    private String assignedTo;
    private LocalDateTime dueAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String result;
    private LocalDateTime createdAt;

    public enum InspectionPriority {
        LOW, MEDIUM, HIGH, URGENT
    }

    public enum InspectionStatus {
        PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public InspectionTask() {
    }

    public InspectionTask(String id, String enterpriseId, String relatedAlertId, String title, String description,
                          InspectionPriority priority, InspectionStatus status, String assignedTo,
                          LocalDateTime dueAt, LocalDateTime startedAt, LocalDateTime completedAt,
                          String result, LocalDateTime createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.relatedAlertId = relatedAlertId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.assignedTo = assignedTo;
        this.dueAt = dueAt;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.result = result;
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

    public String getRelatedAlertId() {
        return relatedAlertId;
    }

    public void setRelatedAlertId(String relatedAlertId) {
        this.relatedAlertId = relatedAlertId;
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

    public InspectionPriority getPriority() {
        return priority;
    }

    public void setPriority(InspectionPriority priority) {
        this.priority = priority;
    }

    public InspectionStatus getStatus() {
        return status;
    }

    public void setStatus(InspectionStatus status) {
        this.status = status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public void setDueAt(LocalDateTime dueAt) {
        this.dueAt = dueAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static InspectionTaskBuilder builder() {
        return new InspectionTaskBuilder();
    }

    public static class InspectionTaskBuilder {
        private String id;
        private String enterpriseId;
        private String relatedAlertId;
        private String title;
        private String description;
        private InspectionPriority priority;
        private InspectionStatus status;
        private String assignedTo;
        private LocalDateTime dueAt;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String result;
        private LocalDateTime createdAt;

        public InspectionTaskBuilder id(String id) {
            this.id = id;
            return this;
        }

        public InspectionTaskBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public InspectionTaskBuilder relatedAlertId(String relatedAlertId) {
            this.relatedAlertId = relatedAlertId;
            return this;
        }

        public InspectionTaskBuilder title(String title) {
            this.title = title;
            return this;
        }

        public InspectionTaskBuilder description(String description) {
            this.description = description;
            return this;
        }

        public InspectionTaskBuilder priority(InspectionPriority priority) {
            this.priority = priority;
            return this;
        }

        public InspectionTaskBuilder status(InspectionStatus status) {
            this.status = status;
            return this;
        }

        public InspectionTaskBuilder assignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public InspectionTaskBuilder dueAt(LocalDateTime dueAt) {
            this.dueAt = dueAt;
            return this;
        }

        public InspectionTaskBuilder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public InspectionTaskBuilder completedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public InspectionTaskBuilder result(String result) {
            this.result = result;
            return this;
        }

        public InspectionTaskBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InspectionTask build() {
            return new InspectionTask(id, enterpriseId, relatedAlertId, title, description, priority, status,
                    assignedTo, dueAt, startedAt, completedAt, result, createdAt);
        }
    }
}
