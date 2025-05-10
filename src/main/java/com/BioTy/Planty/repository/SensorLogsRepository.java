package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.SensorLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SensorLogsRepository extends JpaRepository<SensorLogs, Long> {
    Optional<SensorLogs> findTopByIotDevice_IdOrderByRecordedAtDesc(Long deviceId);
}
