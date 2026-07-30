package com.airline.reservation.service;

import com.airline.reservation.entity.SystemLog;
import com.airline.reservation.repository.SystemLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SystemLogService {

    private final SystemLogRepository systemLogRepository;

    public SystemLogService(SystemLogRepository systemLogRepository) {
        this.systemLogRepository = systemLogRepository;
    }

    public void logEvent(String eventType, String userEmail, String description) {
        SystemLog log = new SystemLog(LocalDateTime.now(), eventType, userEmail, description);
        systemLogRepository.save(log);
    }

    public List<SystemLog> getRecentLogs() {
        return systemLogRepository.findTop20ByOrderByTimestampDesc();
    }

    public List<SystemLog> getAllLogs() {
        return systemLogRepository.findAllByOrderByTimestampDesc();
    }
}
