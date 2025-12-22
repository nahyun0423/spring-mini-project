package com.example.kindergarten.domain.task;

import com.example.kindergarten.domain.env.EnvSnapshot;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "daily_checklist")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyChecklist {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "env_snapshot_id")
    private EnvSnapshot envSnapshot;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "dailyChecklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> items = new ArrayList<>();

    public DailyChecklist(LocalDate date, EnvSnapshot envSnapshot) {
        this.date = date;
        this.envSnapshot = envSnapshot;
    }

    @PrePersist
    void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void addItem(ChecklistItem item) {
        item.attachTo(this);
        this.items.add(item);
    }
}
