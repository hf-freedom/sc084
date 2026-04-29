package com.park.energy.controller;

import com.park.energy.model.ResampleTask;
import com.park.energy.service.ResampleTaskService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resample-tasks")
@CrossOrigin(origins = "*")
public class ResampleTaskController {

    private final ResampleTaskService resampleTaskService;

    public ResampleTaskController(ResampleTaskService resampleTaskService) {
        this.resampleTaskService = resampleTaskService;
    }

    @GetMapping
    public List<ResampleTask> getAllTasks() {
        return resampleTaskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResampleTask> getTaskById(@PathVariable String id) {
        Optional<ResampleTask> task = resampleTaskService.getTaskById(id);
        return task.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ResampleTask> createTask(
            @RequestParam String meterId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime missingFrom,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime missingTo) {
        try {
            ResampleTask task = resampleTaskService.createTask(meterId, missingFrom, missingTo);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ResampleTask> completeTask(@PathVariable String id) {
        try {
            ResampleTask task = resampleTaskService.completeTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/fail")
    public ResponseEntity<ResampleTask> failTask(@PathVariable String id) {
        try {
            ResampleTask task = resampleTaskService.failTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pending")
    public List<ResampleTask> getPendingTasks() {
        return resampleTaskService.getPendingTasks();
    }

    @GetMapping("/meter/{meterId}")
    public List<ResampleTask> getTasksByMeter(@PathVariable String meterId) {
        return resampleTaskService.getTasksByMeter(meterId);
    }
}
