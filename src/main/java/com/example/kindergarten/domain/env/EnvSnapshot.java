package com.example.kindergarten.domain.env;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "env_snapshot")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnvSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private WeatherType weatherType;

    private Integer temperature;

    @Enumerated(EnumType.STRING)
    private PmLevel pm10Level;

    @Enumerated(EnumType.STRING)
    private PmLevel pm25Level;

    @Enumerated(EnumType.STRING)
    private DiseaseStatus diseaseStatus;

    private LocalDateTime createdAt;

    public EnvSnapshot(LocalDate date, WeatherType weatherType, Integer temperature,
                       PmLevel pm10Level, PmLevel pm25Level, DiseaseStatus diseaseStatus) {
        this.date = date;
        this.weatherType = weatherType;
        this.temperature = temperature;
        this.pm10Level = pm10Level;
        this.pm25Level = pm25Level;
        this.diseaseStatus = diseaseStatus;
    }

    @PrePersist
    void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updateDiseaseStatus(DiseaseStatus diseaseStatus) {
        this.diseaseStatus = diseaseStatus;
    }

    public void updateAutoFields(WeatherType weatherType, Integer temperature,
                                 PmLevel pm10Level, PmLevel pm25Level) {
        this.weatherType = weatherType;
        this.temperature = temperature;
        this.pm10Level = pm10Level;
        this.pm25Level = pm25Level;
    }

}
