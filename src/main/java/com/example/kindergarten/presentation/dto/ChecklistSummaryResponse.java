package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.task.DailyChecklist;
import com.example.kindergarten.domain.task.ChecklistItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChecklistSummaryResponse {

    private Long checklistId;
    private LocalDate date;
    private int totalItems;
    private int completedItems;

    public static ChecklistSummaryResponse from(DailyChecklist checklist) {
        List<ChecklistItem> items = checklist.getItems();
        int total = items == null ? 0 : items.size();
        int done = items == null ? 0 : (int) items.stream().filter(ChecklistItem::isCompleted).count();

        return new ChecklistSummaryResponse(checklist.getId(), checklist.getDate(), total, done);
    }
}
