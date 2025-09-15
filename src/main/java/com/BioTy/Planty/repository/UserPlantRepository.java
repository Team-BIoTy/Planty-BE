package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.UserPlant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPlantRepository extends JpaRepository<UserPlant, Long>, UserPlantRepositoryCustom{

    @Query("SELECT up FROM UserPlant up " +
            "JOIN FETCH up.user " +
            "LEFT JOIN FETCH up.iotDevice")
    List<UserPlant> findAllWithUserAndDevice();

}