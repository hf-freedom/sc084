package com.park.energy.repository;

import com.park.energy.model.InspectionTask;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InspectionTaskRepository {

    private final Map<String, InspectionTask> tasks = new ConcurrentHashMap<>();

    public InspectionTask save(InspectionTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<InspectionTask> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<InspectionTask> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<InspectionTask> findByEnterpriseId(String enterpriseId) {
        return tasks.values().stream()
                .filter(t -> enterpriseId.equals(t.getEnterpriseId()))
                .sorted(Comparator.comparing(InspectionTask::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<InspectionTask> findByStatus(InspectionTask.InspectionStatus status) {
        return tasks.values().stream()
                .filter(t -> status == t.getStatus())
                .sorted(Comparator.comparing(InspectionTask::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<InspectionTask> findPendingTasks() {
        return tasks.values().stream()
                .filter(t -> InspectionTask.InspectionStatus.PENDING == t.getStatus()
                        || InspectionTask.InspectionStatus.ASSIGNED == t.getStatus()
                        || InspectionTask.InspectionStatus.IN_PROGRESS == t.getStatus())
                .sorted(Comparator.comparing(InspectionTask::getDueAt))
                .collect(Collectors.toList());
    }

    public boolean existsActiveTaskByEnterpriseId(String enterpriseId) {
        return tasks.values().stream()
                .anyMatch(t -> enterpriseId.equals(t.getEnterpriseId())
                        && (InspectionTask.InspectionStatus.PENDING == t.getStatus()
                        || InspectionTask.InspectionStatus.ASSIGNED == t.getStatus()
                        || InspectionTask.InspectionStatus.IN_PROGRESS == t.getStatus()));
    }

    public void deleteById(String id) {
        tasks.remove(id);
    }
}
