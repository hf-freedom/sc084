package com.park.energy.controller;

import com.park.energy.model.Meter;
import com.park.energy.repository.MeterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/meters")
public class MeterController {

    private final MeterRepository meterRepository;

    public MeterController(MeterRepository meterRepository) {
        this.meterRepository = meterRepository;
    }

    @GetMapping
    public List<Meter> getAllMeters() {
        return meterRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meter> getMeterById(@PathVariable String id) {
        Optional<Meter> meter = meterRepository.findById(id);
        return meter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<Meter> getMetersByEnterprise(@PathVariable String enterpriseId) {
        return meterRepository.findByEnterpriseId(enterpriseId);
    }

    @GetMapping("/active")
    public List<Meter> getActiveMeters() {
        return meterRepository.findActiveMeters();
    }

    @PostMapping
    public Meter createMeter(@RequestBody Meter meter) {
        return meterRepository.save(meter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Meter> updateMeter(@PathVariable String id, @RequestBody Meter meter) {
        if (!meterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        meter.setId(id);
        return ResponseEntity.ok(meterRepository.save(meter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeter(@PathVariable String id) {
        if (!meterRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        meterRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
