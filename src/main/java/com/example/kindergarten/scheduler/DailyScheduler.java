package com.example.kindergarten.scheduler;

import com.example.kindergarten.application.ChecklistGenerationService;
import com.example.kindergarten.application.EnvSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DailyScheduler {

    private final EnvSnapshotService envSnapshotService;
    private final ChecklistGenerationService checklistGenerationService;

    @Scheduled(cron = "0 30 6 * * *")
    public void fetchEnvSnapshot() {
        log.info("[Scheduler] create auto EnvSnapshot");
        envSnapshotService.createAuto(LocalDate.now());
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void generateChecklist() {
        log.info("[Scheduler] generate checklist");
        checklistGenerationService.regenerateForDate(LocalDate.now());
    }
}
