package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.task.ChecklistLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistLogRepository extends JpaRepository<ChecklistLog, Long> {
}
