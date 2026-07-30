package com.airline.reservation.repository;

import com.airline.reservation.entity.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    List<SystemLog> findTop20ByOrderByTimestampDesc();
    List<SystemLog> findAllByOrderByTimestampDesc();
}
