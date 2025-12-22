package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.task.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
    List<ChecklistItem> findByDailyChecklistIdOrderByIdAsc(Long dailyChecklistId);
}
