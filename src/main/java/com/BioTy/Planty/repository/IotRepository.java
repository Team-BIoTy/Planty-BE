package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IotRepository extends JpaRepository<IotDevice, Long> {
    List<IotDevice> findAllByUserId(Long userId);
}
