package com.example.kindergarten.domain.task;

import com.example.kindergarten.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "checklist_item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChecklistItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_checklist_id")
    private DailyChecklist dailyChecklist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_template_id")
    private TaskTemplate taskTemplate;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskCategory category;

    private boolean completed;
    private LocalDateTime completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "completed_by")
    private User completedBy;

    public ChecklistItem(TaskTemplate template) {
        this.taskTemplate = template;
        this.title = template.getTitle();
        this.description = template.getDescription();
        this.category = template.getCategory() != null ? template.getCategory() : TaskCategory.ETC;
        this.completed = false;
    }

    void attachTo(DailyChecklist checklist) {
        this.dailyChecklist = checklist;
    }

    public void complete(User actor) {
        if (this.completed) return;
        this.completed = true;
        this.completedBy = actor;
        this.completedAt = LocalDateTime.now();
    }
}
