package com.example.kindergarten.presentation;

import com.example.kindergarten.application.EnvSnapshotService;
import com.example.kindergarten.domain.env.DiseaseStatus;
import com.example.kindergarten.domain.env.EnvSnapshot;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/env-snapshots")
public class EnvSnapshotController {

    private final EnvSnapshotService envSnapshotService;

    // 자동 생성 - 날짜로 생성
    // POST /env-snapshots/auto?date=2025-12-20
    @PostMapping("/auto")
    public EnvSnapshot createAuto(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {

        return envSnapshotService.createAuto(date);
    }

    // 수동 생성
    @PostMapping("/manual")
    public EnvSnapshot createManual(@RequestBody EnvSnapshotService.ManualRequest req) {
        return envSnapshotService.createManual(req);
    }

    //질병 상태만 수동 수정
    // PATCH /env-snapshots/disease-status?date=2025-12-20&status=FLU_WARNING
    @PatchMapping("/disease-status")
    public EnvSnapshot updateDiseaseStatus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam DiseaseStatus status
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        return envSnapshotService.updateDiseaseStatus(targetDate, status);
    }

    @PatchMapping("/disease-status/body")
    public EnvSnapshot updateDiseaseStatusWithBody(@RequestBody DiseaseStatusManualRequest req) {
        LocalDate targetDate = (req.getDate() != null) ? req.getDate() : LocalDate.now();
        return envSnapshotService.updateDiseaseStatus(targetDate, req.getDiseaseStatus());
    }

    @Getter
    @Setter
    public static class DiseaseStatusManualRequest {
        private LocalDate date;
        private DiseaseStatus diseaseStatus;
    }
}
