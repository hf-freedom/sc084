package com.park.energy.controller;

import com.park.energy.model.InspectionTask;
import com.park.energy.service.InspectionTaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inspection-tasks")
@CrossOrigin(origins = "*")
public class InspectionTaskController {

    private final InspectionTaskService inspectionTaskService;

    public InspectionTaskController(InspectionTaskService inspectionTaskService) {
        this.inspectionTaskService = inspectionTaskService;
    }

    @GetMapping
    public List<InspectionTask> getAllTasks() {
        return inspectionTaskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InspectionTask> getTaskById(@PathVariable String id) {
        Optional<InspectionTask> task = inspectionTaskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InspectionTask> createTask(
            @RequestParam String enterpriseId,
            @RequestParam(required = false) String relatedAlertId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false, defaultValue = "MEDIUM") InspectionTask.InspectionPriority priority) {
        try {
            InspectionTask task = inspectionTaskService.createTask(
                    enterpriseId, relatedAlertId, title, description, priority);
            if (task == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<InspectionTask> assignTask(
            @PathVariable String id,
            @RequestParam String assignedTo) {
        try {
            InspectionTask task = inspectionTaskService.assignTask(id, assignedTo);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<InspectionTask> startTask(@PathVariable String id) {
        try {
            InspectionTask task = inspectionTaskService.startTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<InspectionTask> completeTask(
            @PathVariable String id,
            @RequestParam String result) {
        try {
            InspectionTask task = inspectionTaskService.completeTask(id, result);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<InspectionTask> cancelTask(
            @PathVariable String id,
            @RequestParam String reason) {
        try {
            InspectionTask task = inspectionTaskService.cancelTask(id, reason);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pending")
    public List<InspectionTask> getPendingTasks() {
        return inspectionTaskService.getPendingTasks();
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public List<InspectionTask> getTasksByEnterprise(@PathVariable String enterpriseId) {
        return inspectionTaskService.getTasksByEnterprise(enterpriseId);
    }
}
