package com.example.kindergarten.domain.env;

public enum DiseaseStatus {
    NONE,          // 정상
    FLU_WARNING,   // 독감 주의보
    FLU_ALERT;     // 독감 경보

    public boolean isAtLeast(DiseaseStatus other) {
        return this.ordinal() >= other.ordinal();
    }
    // 현재 상태가 other보다 심각하거나 같냐
}
