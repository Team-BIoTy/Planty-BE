package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.PlantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantInfoRepository extends JpaRepository<PlantInfo, Long> {

}
