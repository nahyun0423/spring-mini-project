package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.env.EnvSnapshot;
import com.example.kindergarten.domain.task.ChecklistItem;
import com.example.kindergarten.domain.task.DailyChecklist;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class TodayChecklistResponse {
    private Long checklistId;
    private LocalDate date;
    private EnvSummary env;
    private List<Item> items;

    @Getter
    @AllArgsConstructor
    public static class EnvSummary {
        private String weatherType;
        private Integer temperature;
        private String pm10Level;
        private String pm25Level;
        private String diseaseStatus;

        public static EnvSummary from(EnvSnapshot e) {
            return new EnvSummary(
                    String.valueOf(e.getWeatherType()),
                    e.getTemperature(),
                    String.valueOf(e.getPm10Level()),
                    String.valueOf(e.getPm25Level()),
                    String.valueOf(e.getDiseaseStatus())
            );
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String title;
        private String description;
        private String category;
        private boolean completed;
        private Long completedBy;
        private LocalDateTime completedAt;

        public static Item from(ChecklistItem i) {
            return new Item(
                    i.getId(),
                    i.getTitle(),
                    i.getDescription(),
                    String.valueOf(i.getCategory()),
                    i.isCompleted(),
                    i.getCompletedBy() != null ? i.getCompletedBy().getId() : null,
                    i.getCompletedAt()
            );
        }
    }

    public static TodayChecklistResponse from(DailyChecklist checklist) {
        EnvSnapshot env = checklist.getEnvSnapshot();
        return new TodayChecklistResponse(
                checklist.getId(),
                checklist.getDate(),
                env != null ? EnvSummary.from(env) : null,
                checklist.getItems().stream().map(Item::from).toList()
        );
    }
}
