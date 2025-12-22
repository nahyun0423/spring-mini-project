package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.task.ChecklistItem;

public class ChecklistItemResponse {

    public String title;
    public String description;
    public boolean completed;

    public ChecklistItemResponse(ChecklistItem item) {
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.completed = item.isCompleted();
    }
}
