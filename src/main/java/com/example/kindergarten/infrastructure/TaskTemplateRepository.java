package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.task.TaskTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTemplateRepository
        extends JpaRepository<TaskTemplate, Long> {

    List<TaskTemplate> findByConditionTagAndActiveTrue(String conditionTag);
}
