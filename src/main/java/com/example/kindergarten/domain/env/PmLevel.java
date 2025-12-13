package com.example.kindergarten.domain.env;

public enum PmLevel {
    // 미세먼지 단계

    GOOD,
    NORMAL,
    BAD,
    VERY_BAD;

    public boolean isWorseOrEqual(PmLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
