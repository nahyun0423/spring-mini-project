package com.example.kindergarten.application;

import com.example.kindergarten.application.external.OpenMeteoClient;
import com.example.kindergarten.domain.env.DiseaseStatus;
import com.example.kindergarten.domain.env.EnvSnapshot;
import com.example.kindergarten.domain.env.PmLevel;
import com.example.kindergarten.domain.env.WeatherType;
import com.example.kindergarten.infrastructure.EnvSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnvSnapshotService {

    private final EnvSnapshotRepository envSnapshotRepository;
    private final OpenMeteoClient openMeteoClient;

    @Transactional
    public EnvSnapshot createAuto(LocalDate date) {
        if (date == null) date = LocalDate.now();

        OpenMeteoClient.AutoEnvData data = openMeteoClient.fetchAutoEnvData(date);

        EnvSnapshot latest = envSnapshotRepository
                .findTopByDateOrderByCreatedAtDesc(date)
                .orElse(null);

        if (latest == null) {
            EnvSnapshot snapshot = new EnvSnapshot(
                    data.date(),
                    data.weatherType(),
                    data.temperature(),
                    data.pm10Level(),
                    data.pm25Level(),
                    DiseaseStatus.NONE
            );
            EnvSnapshot saved = envSnapshotRepository.save(snapshot);
            log.info("[EnvSnapshot] created(auto) id={}, date={}", saved.getId(), saved.getDate());
            return saved;
        }

        latest.updateAutoFields(
                data.weatherType(),
                data.temperature(),
                data.pm10Level(),
                data.pm25Level()
        );

        if (latest.getDiseaseStatus() == null) {
            latest.updateDiseaseStatus(DiseaseStatus.NONE);
        }

        log.info("[EnvSnapshot] updated(auto) id={}, date={}", latest.getId(), latest.getDate());
        return latest;
    }

    @Transactional
    public EnvSnapshot createManual(ManualRequest req) {
        LocalDate date = (req.date() != null) ? req.date() : LocalDate.now();

        EnvSnapshot snapshot = new EnvSnapshot(
                date,
                req.weatherType(),
                req.temperature(),
                req.pm10Level(),
                req.pm25Level(),
                req.diseaseStatus()
        );

        EnvSnapshot saved = envSnapshotRepository.save(snapshot);
        log.info("[EnvSnapshot] created(manual) id={}, date={}", saved.getId(), saved.getDate());
        return saved;
    }

    @Transactional
    public EnvSnapshot updateDiseaseStatus(LocalDate date, DiseaseStatus diseaseStatus) {
        if (date == null) date = LocalDate.now();

        EnvSnapshot latest = envSnapshotRepository
                .findTopByDateOrderByCreatedAtDesc(date)
                .orElse(null);

        if (latest == null) {
            EnvSnapshot snapshot = new EnvSnapshot(
                    date,
                    WeatherType.UNKNOWN,
                    null,
                    null,
                    null,
                    diseaseStatus
            );
            EnvSnapshot saved = envSnapshotRepository.save(snapshot);
            log.info("[EnvSnapshot] created(disease-only) id={}, date={}", saved.getId(), saved.getDate());
            return saved;
        }

        latest.updateDiseaseStatus(diseaseStatus);
        log.info("[EnvSnapshot] updated(disease) id={}, date={}", latest.getId(), latest.getDate());
        return latest;
    }

    public record ManualRequest(
            LocalDate date,
            WeatherType weatherType,
            Integer temperature,
            PmLevel pm10Level,
            PmLevel pm25Level,
            DiseaseStatus diseaseStatus
    ) {}
}
