package com.park.energy.controller;

import com.park.energy.model.EnergyAssessment;
import com.park.energy.service.EnergyAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "*")
public class EnergyAssessmentController {

    private final EnergyAssessmentService energyAssessmentService;

    public EnergyAssessmentController(EnergyAssessmentService energyAssessmentService) {
        this.energyAssessmentService = energyAssessmentService;
    }

    @GetMapping
    public List<EnergyAssessment> getAllAssessments() {
        return energyAssessmentService.getAllAssessments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnergyAssessment> getAssessmentById(@PathVariable String id) {
        Optional<EnergyAssessment> assessment = energyAssessmentService.getAssessmentById(id);
        return assessment.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/assess/{enterpriseId}/{year}/{month}")
    public ResponseEntity<EnergyAssessment> assessEnterprise(
            @PathVariable String enterpriseId,
            @PathVariable Integer year,
            @PathVariable Integer month) {
        try {
            EnergyAssessment assessment = energyAssessmentService.assessEnterprise(enterpriseId, year, month);
            return ResponseEntity.ok(assessment);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/assess-all/{year}/{month}")
    public void assessAllEnterprises(@PathVariable Integer year, @PathVariable Integer month) {
        energyAssessmentService.assessAllEnterprises(year, month);
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<EnergyAssessment> getAssessmentsByEnterprise(@PathVariable String enterpriseId) {
        return energyAssessmentService.getAssessmentsByEnterprise(enterpriseId);
    }

    @GetMapping("/month/{year}/{month}")
    public List<EnergyAssessment> getAssessmentsByMonth(@PathVariable Integer year, @PathVariable Integer month) {
        return energyAssessmentService.getAssessmentsByYearMonth(year, month);
    }

    @GetMapping("/month/{year}/{month}/qualified")
    public List<EnergyAssessment> getQualifiedAssessments(@PathVariable Integer year, @PathVariable Integer month) {
        return energyAssessmentService.getQualifiedAssessments(year, month);
    }
}
