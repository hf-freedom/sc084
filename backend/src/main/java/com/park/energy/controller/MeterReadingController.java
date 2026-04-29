package com.park.energy.controller;

import com.park.energy.model.MeterReading;
import com.park.energy.service.MeterReadingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meter-readings")
@CrossOrigin(origins = "*")
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @GetMapping
    public List<MeterReading> getAllReadings() {
        return meterReadingService.getAllReadings();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeterReading> getReadingById(@PathVariable String id) {
        Optional<MeterReading> reading = meterReadingService.getLatestReadingByMeterId(id);
        return reading.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/submit")
    public ResponseEntity<MeterReading> submitReading(
            @RequestParam String meterId,
            @RequestParam java.math.BigDecimal reading) {
        try {
            MeterReading saved = meterReadingService.submitReading(meterId, reading);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/meter/{meterId}")
    public List<MeterReading> getReadingsByMeter(@PathVariable String meterId) {
        return meterReadingService.getReadingsByMeterId(meterId);
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<MeterReading> getReadingsByEnterprise(@PathVariable String enterpriseId) {
        return meterReadingService.getReadingsByEnterpriseId(enterpriseId);
    }

    @GetMapping("/meter/{meterId}/latest")
    public ResponseEntity<MeterReading> getLatestReading(@PathVariable String meterId) {
        Optional<MeterReading> reading = meterReadingService.getLatestReadingByMeterId(meterId);
        return reading.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
