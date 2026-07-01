package com.niveshcore360.service;

import com.niveshcore360.entity.AuditLog;
import com.niveshcore360.entity.User;
import java.util.List;

/**
 * Service interface for creating and fetching system audit logs.
 */
public interface AuditLogService {
    void log(String action, String details);
    void log(User user, String action, String details);
    List<AuditLog> getLogs();
}
