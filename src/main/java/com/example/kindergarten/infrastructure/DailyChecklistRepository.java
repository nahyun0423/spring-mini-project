package com.example.kindergarten.infrastructure;

import com.example.kindergarten.domain.task.DailyChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface DailyChecklistRepository
        extends JpaRepository<DailyChecklist, Long> {

    Optional<DailyChecklist> findByDate(LocalDate date);

    List<DailyChecklist> findAllByOrderByDateDesc();
}
