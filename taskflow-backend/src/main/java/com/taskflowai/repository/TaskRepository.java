package com.taskflowai.repository;

import com.taskflowai.model.Priority;
import com.taskflowai.model.SourceType;
import com.taskflowai.model.Task;
import com.taskflowai.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {

    Optional<Task> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, TaskStatus status);

    long countByUserIdAndStatusNotAndDueDateBefore(String userId, TaskStatus status, LocalDate date);

    long countByUserIdAndPriority(String userId, Priority priority);

    long countByUserIdAndSourceType(String userId, SourceType sourceType);

    long countByUserIdAndCreatedAtBetween(String userId, Instant start, Instant end);

    long countByUserIdAndStatusAndUpdatedAtBetween(String userId, TaskStatus status, Instant start, Instant end);

    List<Task> findTop5ByUserIdOrderByUpdatedAtDesc(String userId);
}
