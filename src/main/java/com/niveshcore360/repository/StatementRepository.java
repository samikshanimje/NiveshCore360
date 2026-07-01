package com.niveshcore360.repository;

import com.niveshcore360.entity.Statement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Statement archives.
 */
@Repository
public interface StatementRepository extends JpaRepository<Statement, Long> {
    List<Statement> findByUserIdOrderByCreatedAtDesc(Long userId);
}
