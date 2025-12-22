package com.example.kindergarten.presentation;

import com.example.kindergarten.application.ChecklistCompleteService;
import com.example.kindergarten.application.ChecklistQueryService;
import com.example.kindergarten.domain.task.DailyChecklist;
import com.example.kindergarten.presentation.dto.ChecklistSummaryResponse;
import com.example.kindergarten.presentation.dto.CompleteRequest;
import com.example.kindergarten.presentation.dto.TodayChecklistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/checklists")
public class ChecklistController {

    private final ChecklistQueryService checklistQueryService;
    private final ChecklistCompleteService checklistCompleteService;

    // GET /checklists/today
    @GetMapping("/today")
    public ResponseEntity<TodayChecklistResponse> today() {
        DailyChecklist checklist = checklistQueryService.getTodayOrCreate(LocalDate.now());
        return ResponseEntity.ok(TodayChecklistResponse.from(checklist));
    }

    // GET /checklists?date=2025-12-15
    @GetMapping(params = "date")
    public ResponseEntity<TodayChecklistResponse> byDate(@RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        DailyChecklist checklist = checklistQueryService.getTodayOrCreate(date);
        return ResponseEntity.ok(TodayChecklistResponse.from(checklist));
    }

    // GET /checklists  (date 없으면 전체 목록)
    @GetMapping
    public ResponseEntity<List<ChecklistSummaryResponse>> listAll() {
        return ResponseEntity.ok(checklistQueryService.getAllSummaries());
    }

    // GET /checklists/{id}  (상세)
    @GetMapping("/{id}")
    public ResponseEntity<TodayChecklistResponse> detail(@PathVariable Long id) {
        DailyChecklist checklist = checklistQueryService.getById(id);
        return ResponseEntity.ok(TodayChecklistResponse.from(checklist));
    }

    // POST /checklists/items/{id}/complete
    @PostMapping("/items/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id, @RequestBody CompleteRequest req) {
        checklistCompleteService.complete(id, req.getTeacherId());
        return ResponseEntity.ok().build();
    }
}
