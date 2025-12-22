package com.example.kindergarten.presentation;

import com.example.kindergarten.application.ChecklistGenerationService;
import com.example.kindergarten.domain.task.DailyChecklist;
import com.example.kindergarten.presentation.dto.ChecklistGenerateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminChecklistController {

    private final ChecklistGenerationService checklistGenerationService;

    @PostMapping("/checklists/generate-today")
    public ResponseEntity<ChecklistGenerateResponse> generateToday() {
        DailyChecklist checklist = checklistGenerationService.generateTodayChecklist(LocalDate.now());
        return ResponseEntity.ok(new ChecklistGenerateResponse(checklist.getId()));
    }

    // 옵션(필요하면 사용): 강제 재생성
    @PostMapping("/checklists/regenerate")
    public ResponseEntity<ChecklistGenerateResponse> regenerate(@RequestParam("date") String dateStr) {
        DailyChecklist checklist = checklistGenerationService.regenerateForDate(LocalDate.parse(dateStr));
        return ResponseEntity.ok(new ChecklistGenerateResponse(checklist.getId()));
    }
}
