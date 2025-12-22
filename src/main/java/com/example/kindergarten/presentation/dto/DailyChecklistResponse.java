package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.task.DailyChecklist;

import java.time.LocalDate;
import java.util.List;

public class DailyChecklistResponse {

    public Long id;
    public LocalDate date;
    public List<ChecklistItemResponse> items;

    public static DailyChecklistResponse from(
            DailyChecklist checklist,
            List<ChecklistItemResponse> items
    ) {
        DailyChecklistResponse r = new DailyChecklistResponse();
        r.id = checklist.getId();
        r.date = checklist.getDate();
        r.items = items;
        return r;
    }
}
