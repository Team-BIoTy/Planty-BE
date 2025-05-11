package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.DeviceActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceActionLogRepository extends JpaRepository<DeviceActionLog, Long> {
}
