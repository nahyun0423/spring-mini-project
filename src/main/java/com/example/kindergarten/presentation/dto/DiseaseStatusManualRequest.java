package com.example.kindergarten.presentation.dto;

import com.example.kindergarten.domain.env.DiseaseStatus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DiseaseStatusManualRequest {
    private LocalDate date; // null이면 today
    private DiseaseStatus diseaseStatus;
}
