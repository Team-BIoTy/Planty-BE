package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.UserPlant;

import java.util.List;

public interface UserPlantRepositoryCustom {
    List<UserPlant> findAllWithLatestStatusAndPersonalityByUserId(Long userId);
}
