package com.example.kindergarten.domain.task;

import com.example.kindergarten.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "checklist_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChecklistLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_item_id")
    private ChecklistItem checklistItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Enumerated(EnumType.STRING)
    private ChecklistAction action;

    private LocalDateTime createdAt;

    public ChecklistLog(ChecklistItem item, User actor, ChecklistAction action) {
        this.checklistItem = item;
        this.actor = actor;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }
}
