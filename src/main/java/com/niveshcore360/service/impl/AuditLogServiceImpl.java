package com.niveshcore360.service.impl;

import com.niveshcore360.entity.AuditLog;
import com.niveshcore360.entity.User;
import com.niveshcore360.repository.AuditLogRepository;
import com.niveshcore360.security.UserSession;
import com.niveshcore360.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Service implementation for logging system operations.
 */
@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserSession userSession;

    @Autowired
    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, UserSession userSession) {
        this.auditLogRepository = auditLogRepository;
        this.userSession = userSession;
    }

    @Override
    public void log(String action, String details) {
        User currentUser = userSession.getCurrentUser();
        log(currentUser, action, details);
    }

    @Override
    public void log(User user, String action, String details) {
        AuditLog log = AuditLog.builder()
                .user(user)
                .action(action)
                .details(details)
                .build();
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLog> getLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }
}
