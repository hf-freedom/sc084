package com.park.energy.repository;

import com.park.energy.model.ResampleTask;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class ResampleTaskRepository {

    private final Map<String, ResampleTask> tasks = new ConcurrentHashMap<>();

    public ResampleTask save(ResampleTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<ResampleTask> findById(String id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<ResampleTask> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<ResampleTask> findByMeterId(String meterId) {
        return tasks.values().stream()
                .filter(t -> meterId.equals(t.getMeterId()))
                .sorted(Comparator.comparing(ResampleTask::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<ResampleTask> findByStatus(ResampleTask.ResampleStatus status) {
        return tasks.values().stream()
                .filter(t -> status == t.getStatus())
                .sorted(Comparator.comparing(ResampleTask::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<ResampleTask> findPendingTasks() {
        return tasks.values().stream()
                .filter(t -> ResampleTask.ResampleStatus.PENDING == t.getStatus())
                .sorted(Comparator.comparing(ResampleTask::getCreatedAt))
                .collect(Collectors.toList());
    }

    public void deleteById(String id) {
        tasks.remove(id);
    }
}
