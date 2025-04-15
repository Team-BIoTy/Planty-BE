package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.repository.UserPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPlantService {
    private final UserPlantRepository userPlantRepository;

    public List<UserPlantSummaryResponseDto> getUserPlants(Long userId){
        return userPlantRepository.findSummaryDtoByUserId(userId);
    }
}
