package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MeterReading implements Serializable {
    private String id;
    private String meterId;
    private String enterpriseId;
    private Meter.MeterType type;
    private BigDecimal reading;
    private BigDecimal consumption;
    private LocalDateTime readingTime;
    private Boolean isValid;
    private LocalDateTime createdAt;

    public MeterReading() {
    }

    public MeterReading(String id, String meterId, String enterpriseId, Meter.MeterType type,
                        BigDecimal reading, BigDecimal consumption, LocalDateTime readingTime,
                        Boolean isValid, LocalDateTime createdAt) {
        this.id = id;
        this.meterId = meterId;
        this.enterpriseId = enterpriseId;
        this.type = type;
        this.reading = reading;
        this.consumption = consumption;
        this.readingTime = readingTime;
        this.isValid = isValid;
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

    public Meter.MeterType getType() {
        return type;
    }

    public void setType(Meter.MeterType type) {
        this.type = type;
    }

    public BigDecimal getReading() {
        return reading;
    }

    public void setReading(BigDecimal reading) {
        this.reading = reading;
    }

    public BigDecimal getConsumption() {
        return consumption;
    }

    public void setConsumption(BigDecimal consumption) {
        this.consumption = consumption;
    }

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static MeterReadingBuilder builder() {
        return new MeterReadingBuilder();
    }

    public static class MeterReadingBuilder {
        private String id;
        private String meterId;
        private String enterpriseId;
        private Meter.MeterType type;
        private BigDecimal reading;
        private BigDecimal consumption;
        private LocalDateTime readingTime;
        private Boolean isValid;
        private LocalDateTime createdAt;

        public MeterReadingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public MeterReadingBuilder meterId(String meterId) {
            this.meterId = meterId;
            return this;
        }

        public MeterReadingBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public MeterReadingBuilder type(Meter.MeterType type) {
            this.type = type;
            return this;
        }

        public MeterReadingBuilder reading(BigDecimal reading) {
            this.reading = reading;
            return this;
        }

        public MeterReadingBuilder consumption(BigDecimal consumption) {
            this.consumption = consumption;
            return this;
        }

        public MeterReadingBuilder readingTime(LocalDateTime readingTime) {
            this.readingTime = readingTime;
            return this;
        }

        public MeterReadingBuilder isValid(Boolean isValid) {
            this.isValid = isValid;
            return this;
        }

        public MeterReadingBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public MeterReading build() {
            return new MeterReading(id, meterId, enterpriseId, type, reading, consumption, readingTime, isValid, createdAt);
        }
    }
}
