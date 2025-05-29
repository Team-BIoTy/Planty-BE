package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.DeviceCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {
    @Query("""
        SELECT dc.userPlant.id, dc.commandType
        FROM DeviceCommand dc
        WHERE dc.status = 'RUNNING'
          AND dc.willBeTurnedOffAt > :now
          AND dc.userPlant.id IN :userPlantIds
    """)
    List<Object[]> findRunningCommandsByUserPlantIds(
            @Param("userPlantIds") List<Long> userPlantIds,
            @Param("now") LocalDateTime now
    );

    Optional<DeviceCommand> findTopByUserPlant_IdOrderByWillBeTurnedOffAtDesc(Long userPlantId);
}
