package com.example.kindergarten.application;

import com.example.kindergarten.domain.env.DiseaseStatus;
import com.example.kindergarten.domain.env.EnvSnapshot;
import com.example.kindergarten.domain.env.PmLevel;
import com.example.kindergarten.domain.env.WeatherType;
import com.example.kindergarten.domain.task.ChecklistItem;
import com.example.kindergarten.domain.task.DailyChecklist;
import com.example.kindergarten.domain.task.TaskTemplate;
import com.example.kindergarten.infrastructure.DailyChecklistRepository;
import com.example.kindergarten.infrastructure.EnvSnapshotRepository;
import com.example.kindergarten.infrastructure.TaskTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChecklistGenerationService {

    private final EnvSnapshotRepository envSnapshotRepository;
    private final TaskTemplateRepository taskTemplateRepository;
    private final DailyChecklistRepository dailyChecklistRepository;

    @Transactional
    public DailyChecklist generateTodayChecklist(LocalDate date) {
        return dailyChecklistRepository.findByDate(date)
                .map(existing -> {
                    if (existing.getItems() == null || existing.getItems().isEmpty()) {
                        dailyChecklistRepository.delete(existing);
                        return createChecklistFromEnv(date);
                    }
                    return existing;
                })
                .orElseGet(() -> createChecklistFromEnv(date));
    }

    @Transactional
    public DailyChecklist regenerateForDate(LocalDate date) {
        dailyChecklistRepository.findByDate(date).ifPresent(dailyChecklistRepository::delete);
        return createChecklistFromEnv(date);
    }

    private DailyChecklist createChecklistFromEnv(LocalDate date) {
        EnvSnapshot env = envSnapshotRepository.findTopByDateOrderByCreatedAtDesc(date)
                .orElseThrow(() -> new IllegalStateException("해당 날짜 EnvSnapshot 없음: " + date));

        List<TaskTemplate> templates = resolveTemplates(env);

        DailyChecklist checklist = new DailyChecklist(date, env);
        for (TaskTemplate t : templates) {
            checklist.addItem(new ChecklistItem(t));
        }

        return dailyChecklistRepository.save(checklist);
    }

    private List<TaskTemplate> resolveTemplates(EnvSnapshot env) {
        Set<TaskTemplate> result = new LinkedHashSet<>();

        addByTag(result, "DAILY");

        PmLevel pm10 = env.getPm10Level();
        PmLevel pm25 = env.getPm25Level();

        boolean anyVeryBad = (pm10 != null && pm10 == PmLevel.VERY_BAD) || (pm25 != null && pm25 == PmLevel.VERY_BAD);
        boolean anyBadOrWorse = (pm10 != null && pm10.isBadOrWorse()) || (pm25 != null && pm25.isBadOrWorse());

        if (anyVeryBad) {
            addByTag(result, "PM_BAD");
            addByTag(result, "PM_VERY_BAD");
        } else if (anyBadOrWorse) {
            addByTag(result, "PM_BAD");
        }

        DiseaseStatus disease = env.getDiseaseStatus();
        if (disease != null) {
            if (disease.isAtLeast(DiseaseStatus.FLU_ALERT)) {
                addByTag(result, "FLU_WARNING");
                addByTag(result, "FLU_ALERT");
            } else if (disease.isAtLeast(DiseaseStatus.FLU_WARNING)) {
                addByTag(result, "FLU_WARNING");
            }
        }

        WeatherType weatherType = env.getWeatherType();
        if (weatherType == WeatherType.RAINY) {
            addByTag(result, "RAINY");
        } else if (weatherType == WeatherType.SNOWY) {
            addByTag(result, "SNOWY");
        }

        int temp = env.getTemperature();
        if (temp <= 5) {
            addByTag(result, "COLD");
        } else if (temp >= 28) {
            addByTag(result, "HOT");
        }

        return new ArrayList<>(result);
    }

    private void addByTag(Set<TaskTemplate> target, String tag) {
        target.addAll(taskTemplateRepository.findByConditionTagAndActiveTrue(tag));
    }
}
