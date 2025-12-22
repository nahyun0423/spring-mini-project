package com.example.kindergarten.domain.task;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "task_template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaskTemplate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String conditionTag;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskCategory category;

    private boolean active = true;

    public TaskTemplate(String conditionTag, String title, String description, TaskCategory category, boolean active) {
        this.conditionTag = conditionTag;
        this.title = title;
        this.description = description;
        this.category = category;
        this.active = active;
    }
}
