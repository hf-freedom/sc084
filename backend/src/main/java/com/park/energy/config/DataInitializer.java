package com.park.energy.config;

import com.park.energy.model.Enterprise;
import com.park.energy.model.Meter;
import com.park.energy.repository.EnterpriseRepository;
import com.park.energy.repository.MeterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EnterpriseRepository enterpriseRepository;
    private final MeterRepository meterRepository;

    public DataInitializer(EnterpriseRepository enterpriseRepository, MeterRepository meterRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.meterRepository = meterRepository;
    }

    @Override
    public void run(String... args) {
        if (enterpriseRepository.findAll().isEmpty()) {
            initializeEnterprises();
        }
        if (meterRepository.findAll().isEmpty()) {
            initializeMeters();
        }
    }

    private void initializeEnterprises() {
        Enterprise e1 = Enterprise.builder()
                .name("科技创新有限公司")
                .industryType("制造业")
                .monthlyElectricityQuota(new BigDecimal("10000"))
                .monthlyWaterQuota(new BigDecimal("500"))
                .dailyElectricityQuota(new BigDecimal("333.33"))
                .dailyWaterQuota(new BigDecimal("16.67"))
                .keyEnterprise(true)
                .servicesRestricted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        enterpriseRepository.save(e1);

        Enterprise e2 = Enterprise.builder()
                .name("绿能环保有限公司")
                .industryType("服务业")
                .monthlyElectricityQuota(new BigDecimal("5000"))
                .monthlyWaterQuota(new BigDecimal("300"))
                .dailyElectricityQuota(new BigDecimal("166.67"))
                .dailyWaterQuota(new BigDecimal("10"))
                .keyEnterprise(false)
                .servicesRestricted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        enterpriseRepository.save(e2);

        Enterprise e3 = Enterprise.builder()
                .name("精密制造集团")
                .industryType("制造业")
                .monthlyElectricityQuota(new BigDecimal("20000"))
                .monthlyWaterQuota(new BigDecimal("1000"))
                .dailyElectricityQuota(new BigDecimal("666.67"))
                .dailyWaterQuota(new BigDecimal("33.33"))
                .keyEnterprise(true)
                .servicesRestricted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        enterpriseRepository.save(e3);

        Enterprise e4 = Enterprise.builder()
                .name("智慧物流中心")
                .industryType("物流")
                .monthlyElectricityQuota(new BigDecimal("8000"))
                .monthlyWaterQuota(new BigDecimal("200"))
                .dailyElectricityQuota(new BigDecimal("266.67"))
                .dailyWaterQuota(new BigDecimal("6.67"))
                .keyEnterprise(false)
                .servicesRestricted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        enterpriseRepository.save(e4);

        Enterprise e5 = Enterprise.builder()
                .name("生物医药研究院")
                .industryType("科研")
                .monthlyElectricityQuota(new BigDecimal("15000"))
                .monthlyWaterQuota(new BigDecimal("800"))
                .dailyElectricityQuota(new BigDecimal("500"))
                .dailyWaterQuota(new BigDecimal("26.67"))
                .keyEnterprise(true)
                .servicesRestricted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        enterpriseRepository.save(e5);
    }

    private void initializeMeters() {
        enterpriseRepository.findAll().forEach(enterprise -> {
            Meter electricityMeter1 = Meter.builder()
                    .enterpriseId(enterprise.getId())
                    .name(enterprise.getName() + " - 电表1")
                    .type(Meter.MeterType.ELECTRICITY)
                    .location("主车间")
                    .lastReading(new BigDecimal("0"))
                    .lastReadingTime(LocalDateTime.now())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            meterRepository.save(electricityMeter1);

            Meter electricityMeter2 = Meter.builder()
                    .enterpriseId(enterprise.getId())
                    .name(enterprise.getName() + " - 电表2")
                    .type(Meter.MeterType.ELECTRICITY)
                    .location("办公区")
                    .lastReading(new BigDecimal("0"))
                    .lastReadingTime(LocalDateTime.now())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            meterRepository.save(electricityMeter2);

            Meter waterMeter = Meter.builder()
                    .enterpriseId(enterprise.getId())
                    .name(enterprise.getName() + " - 水表")
                    .type(Meter.MeterType.WATER)
                    .location("主管道")
                    .lastReading(new BigDecimal("0"))
                    .lastReadingTime(LocalDateTime.now())
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .build();
            meterRepository.save(waterMeter);
        });
    }
}
