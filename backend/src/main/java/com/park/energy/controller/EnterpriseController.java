package com.park.energy.controller;

import com.park.energy.model.Enterprise;
import com.park.energy.repository.EnterpriseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enterprises")
@CrossOrigin(origins = "*")
public class EnterpriseController {

    private final EnterpriseRepository enterpriseRepository;

    public EnterpriseController(EnterpriseRepository enterpriseRepository) {
        this.enterpriseRepository = enterpriseRepository;
    }

    @GetMapping
    public List<Enterprise> getAllEnterprises() {
        return enterpriseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enterprise> getEnterpriseById(@PathVariable String id) {
        Optional<Enterprise> enterprise = enterpriseRepository.findById(id);
        return enterprise.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Enterprise createEnterprise(@RequestBody Enterprise enterprise) {
        return enterpriseRepository.save(enterprise);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Enterprise> updateEnterprise(@PathVariable String id, @RequestBody Enterprise enterprise) {
        Optional<Enterprise> existing = enterpriseRepository.findById(id);
        if (!existing.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        enterprise.setId(id);
        return ResponseEntity.ok(enterpriseRepository.save(enterprise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable String id) {
        if (!enterpriseRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        enterpriseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/key-enterprises")
    public List<Enterprise> getKeyEnterprises() {
        return enterpriseRepository.findKeyEnterprises();
    }

    @GetMapping("/industry/{industryType}")
    public List<Enterprise> getEnterprisesByIndustry(@PathVariable String industryType) {
        return enterpriseRepository.findByIndustryType(industryType);
    }
}
