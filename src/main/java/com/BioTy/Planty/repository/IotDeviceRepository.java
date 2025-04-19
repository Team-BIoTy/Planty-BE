package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IotDeviceRepository extends JpaRepository<IotDevice, Long> {
}
