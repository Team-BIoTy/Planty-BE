package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.PlantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlantStatusRepository extends JpaRepository<PlantStatus, Long> {
    Optional<PlantStatus> findTopByUserPlant_IdOrderByCheckedAtDesc(Long userPlantId);
}
