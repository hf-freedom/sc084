package com.park.energy.service;

import com.park.energy.model.Meter;
import com.park.energy.model.MeterReading;
import com.park.energy.repository.MeterReadingRepository;
import com.park.energy.repository.MeterRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MeterReadingService {

    private final MeterRepository meterRepository;
    private final MeterReadingRepository meterReadingRepository;

    public MeterReadingService(MeterRepository meterRepository, MeterReadingRepository meterReadingRepository) {
        this.meterRepository = meterRepository;
        this.meterReadingRepository = meterReadingRepository;
    }

    public MeterReading submitReading(String meterId, BigDecimal reading) {
        Optional<Meter> meterOpt = meterRepository.findById(meterId);
        if (!meterOpt.isPresent()) {
            throw new RuntimeException("Meter not found: " + meterId);
        }

        Meter meter = meterOpt.get();
        if (!meter.getActive()) {
            throw new RuntimeException("Meter is not active: " + meterId);
        }

        BigDecimal consumption = BigDecimal.ZERO;
        if (meter.getLastReading() != null && reading.compareTo(meter.getLastReading()) >= 0) {
            consumption = reading.subtract(meter.getLastReading());
        }

        MeterReading meterReading = MeterReading.builder()
                .meterId(meterId)
                .enterpriseId(meter.getEnterpriseId())
                .type(meter.getType())
                .reading(reading)
                .consumption(consumption)
                .readingTime(LocalDateTime.now())
                .isValid(true)
                .build();

        MeterReading saved = meterReadingRepository.save(meterReading);

        meter.setLastReading(reading);
        meter.setLastReadingTime(LocalDateTime.now());
        meterRepository.save(meter);

        return saved;
    }

    public List<MeterReading> getReadingsByMeterId(String meterId) {
        return meterReadingRepository.findByMeterId(meterId);
    }

    public List<MeterReading> getReadingsByEnterpriseId(String enterpriseId) {
        return meterReadingRepository.findByEnterpriseId(enterpriseId);
    }

    public Optional<MeterReading> getLatestReadingByMeterId(String meterId) {
        return meterReadingRepository.findLatestByMeterId(meterId);
    }

    public List<MeterReading> getAllReadings() {
        return meterReadingRepository.findAll();
    }
}
