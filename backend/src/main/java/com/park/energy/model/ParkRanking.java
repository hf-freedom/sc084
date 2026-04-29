package com.park.energy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ParkRanking implements Serializable {
    private String id;
    private String enterpriseId;
    private String enterpriseName;
    private Integer year;
    private Integer month;
    private Integer rank;
    private RankingType type;
    private BigDecimal value;
    private LocalDate createdAt;

    public enum RankingType {
        ELECTRICITY_CONSUMPTION, WATER_CONSUMPTION, TOTAL_CONSUMPTION
    }

    public ParkRanking() {
    }

    public ParkRanking(String id, String enterpriseId, String enterpriseName, Integer year, Integer month,
                       Integer rank, RankingType type, BigDecimal value, LocalDate createdAt) {
        this.id = id;
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.year = year;
        this.month = month;
        this.rank = rank;
        this.type = type;
        this.value = value;
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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public RankingType getType() {
        return type;
    }

    public void setType(RankingType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public static ParkRankingBuilder builder() {
        return new ParkRankingBuilder();
    }

    public static class ParkRankingBuilder {
        private String id;
        private String enterpriseId;
        private String enterpriseName;
        private Integer year;
        private Integer month;
        private Integer rank;
        private RankingType type;
        private BigDecimal value;
        private LocalDate createdAt;

        public ParkRankingBuilder id(String id) {
            this.id = id;
            return this;
        }

        public ParkRankingBuilder enterpriseId(String enterpriseId) {
            this.enterpriseId = enterpriseId;
            return this;
        }

        public ParkRankingBuilder enterpriseName(String enterpriseName) {
            this.enterpriseName = enterpriseName;
            return this;
        }

        public ParkRankingBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public ParkRankingBuilder month(Integer month) {
            this.month = month;
            return this;
        }

        public ParkRankingBuilder rank(Integer rank) {
            this.rank = rank;
            return this;
        }

        public ParkRankingBuilder type(RankingType type) {
            this.type = type;
            return this;
        }

        public ParkRankingBuilder value(BigDecimal value) {
            this.value = value;
            return this;
        }

        public ParkRankingBuilder createdAt(LocalDate createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public ParkRanking build() {
            return new ParkRanking(id, enterpriseId, enterpriseName, year, month, rank, type, value, createdAt);
        }
    }
}
