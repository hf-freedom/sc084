package com.park.energy.service;

import com.park.energy.model.InspectionTask;
import com.park.energy.repository.InspectionTaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InspectionTaskService {

    private final InspectionTaskRepository inspectionTaskRepository;

    public InspectionTaskService(InspectionTaskRepository inspectionTaskRepository) {
        this.inspectionTaskRepository = inspectionTaskRepository;
    }

    public InspectionTask createTask(String enterpriseId, String relatedAlertId,
                                      String title, String description, InspectionTask.InspectionPriority priority) {
        if (inspectionTaskRepository.existsActiveTaskByEnterpriseId(enterpriseId)) {
            return null;
        }

        InspectionTask task = InspectionTask.builder()
                .enterpriseId(enterpriseId)
                .relatedAlertId(relatedAlertId)
                .title(title)
                .description(description)
                .priority(priority)
                .status(InspectionTask.InspectionStatus.PENDING)
                .dueAt(LocalDateTime.now().plusDays(3))
                .build();

        return inspectionTaskRepository.save(task);
    }

    public InspectionTask assignTask(String taskId, String assignedTo) {
        Optional<InspectionTask> taskOpt = inspectionTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        InspectionTask task = taskOpt.get();
        task.setStatus(InspectionTask.InspectionStatus.ASSIGNED);
        task.setAssignedTo(assignedTo);

        return inspectionTaskRepository.save(task);
    }

    public InspectionTask startTask(String taskId) {
        Optional<InspectionTask> taskOpt = inspectionTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        InspectionTask task = taskOpt.get();
        task.setStatus(InspectionTask.InspectionStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());

        return inspectionTaskRepository.save(task);
    }

    public InspectionTask completeTask(String taskId, String result) {
        Optional<InspectionTask> taskOpt = inspectionTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        InspectionTask task = taskOpt.get();
        task.setStatus(InspectionTask.InspectionStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setResult(result);

        return inspectionTaskRepository.save(task);
    }

    public InspectionTask cancelTask(String taskId, String reason) {
        Optional<InspectionTask> taskOpt = inspectionTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            throw new RuntimeException("Task not found: " + taskId);
        }

        InspectionTask task = taskOpt.get();
        task.setStatus(InspectionTask.InspectionStatus.CANCELLED);
        task.setResult("Cancelled: " + reason);
        task.setCompletedAt(LocalDateTime.now());

        return inspectionTaskRepository.save(task);
    }

    public List<InspectionTask> getPendingTasks() {
        return inspectionTaskRepository.findPendingTasks();
    }

    public List<InspectionTask> getTasksByEnterprise(String enterpriseId) {
        return inspectionTaskRepository.findByEnterpriseId(enterpriseId);
    }

    public List<InspectionTask> getAllTasks() {
        return inspectionTaskRepository.findAll();
    }

    public Optional<InspectionTask> getTaskById(String id) {
        return inspectionTaskRepository.findById(id);
    }
}
