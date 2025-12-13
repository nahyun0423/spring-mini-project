package com.example.kindergarten.domain.env;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "env_snapshot")
public class EnvSnapshot {
// “해당 날짜 기준 환경 상태(날씨, 미세먼지, 질병 상태 등)”를 저장하는 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 기준 날짜 (오늘 날짜)
    private LocalDate date;

    // 날씨 타입 (맑음/흐림/비 등)
    @Enumerated(EnumType.STRING)
    private WeatherType weatherType;

    // 기온 ( 대략 정수로 )
    private Integer temperature;

    // PM10 단계 - 미세먼지
    @Enumerated(EnumType.STRING)
    private PmLevel pm10Level;

    // PM2.5 단계 - 초미세먼지
    @Enumerated(EnumType.STRING)
    private PmLevel pm25Level;

    // 주요 질병 상태 (예: 독감 주의보)
    @Enumerated(EnumType.STRING)
    private DiseaseStatus diseaseStatus;

    // 레코드 생성 시각
    private LocalDateTime createdAt;

    protected EnvSnapshot() {
        // JPA 기본 생성자
    }

    public EnvSnapshot(LocalDate date,
                       WeatherType weatherType,
                       Integer temperature,
                       PmLevel pm10Level,
                       PmLevel pm25Level,
                       DiseaseStatus diseaseStatus) {
        this.date = date;
        this.weatherType = weatherType;
        this.temperature = temperature;
        this.pm10Level = pm10Level;
        this.pm25Level = pm25Level;
        this.diseaseStatus = diseaseStatus;
    }

    @PrePersist
    public void onPrePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
