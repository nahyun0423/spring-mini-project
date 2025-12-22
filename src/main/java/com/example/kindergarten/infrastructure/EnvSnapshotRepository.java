package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.env.EnvSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EnvSnapshotRepository
        extends JpaRepository<EnvSnapshot, Long> {

    Optional<EnvSnapshot> findTopByDateOrderByCreatedAtDesc(LocalDate date);
}
