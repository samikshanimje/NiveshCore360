package com.niveshcore360.repository;

import com.niveshcore360.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for outgoing Email logs.
 */
@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
    List<EmailLog> findByRecipientOrderBySentAtDesc(String recipient);
}
