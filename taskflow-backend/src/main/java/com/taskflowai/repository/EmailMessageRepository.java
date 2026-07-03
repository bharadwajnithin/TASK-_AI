package com.taskflowai.repository;

import com.taskflowai.model.EmailMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EmailMessageRepository extends MongoRepository<EmailMessage, String> {

    Page<EmailMessage> findByUserIdOrderByReceivedAtDesc(String userId, Pageable pageable);

    Optional<EmailMessage> findByIdAndUserId(String id, String userId);

    boolean existsByUserIdAndGmailMessageId(String userId, String gmailMessageId);

    long countByUserIdAndProcessedFalse(String userId);
}
