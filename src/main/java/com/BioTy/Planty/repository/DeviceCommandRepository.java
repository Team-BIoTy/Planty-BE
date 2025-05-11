package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {
}
