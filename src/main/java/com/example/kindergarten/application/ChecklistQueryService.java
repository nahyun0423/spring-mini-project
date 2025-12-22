package com.example.kindergarten.application;

import com.example.kindergarten.domain.task.DailyChecklist;
import com.example.kindergarten.infrastructure.DailyChecklistRepository;
import com.example.kindergarten.presentation.dto.ChecklistSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistQueryService {

    private final DailyChecklistRepository dailyChecklistRepository;
    private final ChecklistGenerationService generationService;

    @Transactional
    public DailyChecklist getTodayOrCreate(LocalDate date) {
        return dailyChecklistRepository.findByDate(date)
                .orElseGet(() -> generationService.generateTodayChecklist(date));
    }

    @Transactional(readOnly = true)
    public List<ChecklistSummaryResponse> getAllSummaries() {
        return dailyChecklistRepository.findAllByOrderByDateDesc()
                .stream()
                .map(ChecklistSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DailyChecklist getById(Long id) {
        return dailyChecklistRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Checklist 없음: " + id));
    }
}
