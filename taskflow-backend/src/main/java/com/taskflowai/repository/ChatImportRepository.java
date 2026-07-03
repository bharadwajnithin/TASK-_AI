package com.taskflowai.repository;

import com.taskflowai.model.ChatImport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatImportRepository extends MongoRepository<ChatImport, String> {

    Page<ChatImport> findByUserIdOrderByImportedAtDesc(String userId, Pageable pageable);

    Optional<ChatImport> findByIdAndUserId(String id, String userId);

    long countByUserIdAndProcessedFalse(String userId);
}
