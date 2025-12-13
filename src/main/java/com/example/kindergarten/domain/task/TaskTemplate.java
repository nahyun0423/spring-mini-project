package com.example.kindergarten.domain.task;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "task_template")
public class TaskTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 조건에 해당하는 템플릿인지 (예: "PM_BAD", "FLU_WARNING")
    @Column(nullable = false)
    private String conditionTag;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskCategory category;

    private boolean active = true;

    protected TaskTemplate() {
    }

    public TaskTemplate(String conditionTag,
                        String title,
                        String description,
                        TaskCategory category) {
        this.conditionTag = conditionTag;
        this.title = title;
        this.description = description;
        this.category = category;
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }
}

