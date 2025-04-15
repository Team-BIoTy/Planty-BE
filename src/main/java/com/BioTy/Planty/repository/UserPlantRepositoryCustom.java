package com.BioTy.Planty.repository;

import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;

import java.util.List;

public interface UserPlantRepositoryCustom {
    List<UserPlantSummaryResponseDto> findSummaryDtoByUserId(Long userId);
}
