package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.SensorLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SensorLogsRepository extends JpaRepository<SensorLogs, Long> {
    Optional<SensorLogs> findTopByIotDevice_IdOrderByRecordedAtDesc(Long deviceId);

    List<SensorLogs> findByIotDevice_IdAndRecordedAtBetweenOrderByRecordedAtAsc(
            Long deviceId, LocalDateTime start, LocalDateTime end
    );
}
