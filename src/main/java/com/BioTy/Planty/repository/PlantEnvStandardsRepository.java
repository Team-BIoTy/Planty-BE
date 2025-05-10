package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.PlantEnvStandards;
import com.BioTy.Planty.entity.PlantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantEnvStandardsRepository extends JpaRepository<PlantEnvStandards, Long> {
    Optional<PlantEnvStandards> findByPlantInfo(PlantInfo plantInfo);
}
