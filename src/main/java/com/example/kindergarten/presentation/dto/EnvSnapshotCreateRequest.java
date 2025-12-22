package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.env.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EnvSnapshotCreateRequest {
    private LocalDate date;
    private WeatherType weatherType;
    private Integer temperature;
    private PmLevel pm10Level;
    private PmLevel pm25Level;
    private DiseaseStatus diseaseStatus;
}
