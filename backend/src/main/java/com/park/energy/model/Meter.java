package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Meter implements Serializable {
    private String id;
    private String enterpriseId;
    private String name;
    private MeterType type;
    private String location;
    private BigDecimal lastReading;
    private LocalDateTime lastReadingTime;
    private Boolean active;
    private LocalDateTime createdAt;

    public enum MeterType {
        ELECTRICITY, WATER
    }

    public Meter() {
    }

    public Meter(String id, String enterpriseId, String name, MeterType type, String location,
                 BigDecimal lastReading, LocalDateTime lastReadingTime, Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.name = name;
        this.type = type;
        this.location = location;
        this.lastReading = lastReading;
        this.lastReadingTime = lastReadingTime;
        this.active = active;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MeterType getType() {
        return type;
    }

    public void setType(MeterType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BigDecimal getLastReading() {
        return lastReading;
    }

    public void setLastReading(BigDecimal lastReading) {
        this.lastReading = lastReading;
    }

    public LocalDateTime getLastReadingTime() {
        return lastReadingTime;
    }

    public void setLastReadingTime(LocalDateTime lastReadingTime) {
        this.lastReadingTime = lastReadingTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static MeterBuilder builder() {
        return new MeterBuilder();
    }

    public static class MeterBuilder {
        private String id;
        private String enterpriseId;
        private String name;
        private MeterType type;
        private String location;
        private BigDecimal lastReading;
        private LocalDateTime lastReadingTime;
        private Boolean active;
        private LocalDateTime createdAt;

        public MeterBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MeterBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public MeterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public MeterBuilder type(MeterType type) {
            this.type = type;
            return this;
        }

        public MeterBuilder location(String location) {
            this.location = location;
            return this;
        }

        public MeterBuilder lastReading(BigDecimal lastReading) {
            this.lastReading = lastReading;
            return this;
        }

        public MeterBuilder lastReadingTime(LocalDateTime lastReadingTime) {
            this.lastReadingTime = lastReadingTime;
            return this;
        }

        public MeterBuilder active(Boolean active) {
            this.active = active;
            return this;
        }

        public MeterBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Meter build() {
            return new Meter(id, enterpriseId, name, type, location, lastReading, lastReadingTime, active, createdAt);
        }
    }
}
