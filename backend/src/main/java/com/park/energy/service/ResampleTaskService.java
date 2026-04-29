package com.park.energy.service;

import com.park.energy.model.Meter;
import com.park.energy.model.ResampleTask;
import com.park.energy.repository.MeterRepository;
import com.park.energy.repository.ResampleTaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResampleTaskService {

    private final ResampleTaskRepository resampleTaskRepository;
    private final MeterRepository meterRepository;

    public ResampleTaskService(ResampleTaskRepository resampleTaskRepository,
                                MeterRepository meterRepository) {
        this.resampleTaskRepository = resampleTaskRepository;
        this.meterRepository = meterRepository;
    }

    public ResampleTask createTask(String meterId, LocalDateTime missingFrom, LocalDateTime missingTo) {
        Optional<Meter> meterOpt = meterRepository.findById(meterId);
        if (!meterOpt.isPresent()) {
            throw new RuntimeException("Meter not found: " + meterId);
        }

        Meter meter = meterOpt.get();

        ResampleTask task = ResampleTask.builder()
                .meterId(meterId)
                .enterpriseId(meter.getEnterpriseId())
                .missingFrom(missingFrom)
                .missingTo(missingTo)
                .status(ResampleTask.ResampleStatus.PENDING)
                .build();

        return resampleTaskRepository.save(task);
    }

    public ResampleTask completeTask(String taskId) {
        Optional<ResampleTask> taskOpt = resampleTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        ResampleTask task = taskOpt.get();
        task.setStatus(ResampleTask.ResampleStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());

        return resampleTaskRepository.save(task);
    }

    public ResampleTask failTask(String taskId) {
        Optional<ResampleTask> taskOpt = resampleTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        ResampleTask task = taskOpt.get();
        task.setStatus(ResampleTask.ResampleStatus.FAILED);
        task.setCompletedAt(LocalDateTime.now());

        return resampleTaskRepository.save(task);
    }

    public List<ResampleTask> getPendingTasks() {
        return resampleTaskRepository.findPendingTasks();
    }

    public List<ResampleTask> getTasksByMeter(String meterId) {
        return resampleTaskRepository.findByMeterId(meterId);
    }

    public List<ResampleTask> getAllTasks() {
        return resampleTaskRepository.findAll();
    }

    public Optional<ResampleTask> getTaskById(String id) {
        return resampleTaskRepository.findById(id);
    }
}
