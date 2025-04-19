package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.plant.PlantInfoListResponseDto;
import com.BioTy.Planty.entity.PlantInfo;
import com.BioTy.Planty.repository.PlantInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantInfoService {
    private final PlantInfoRepository plantInfoRepository;

    // 전체 식물 목록(도감) 불러오기 메서드
    public List<PlantInfoListResponseDto> getAllPlantInfoLists(){
        List<PlantInfo> plantInfos = plantInfoRepository.findAll();
        return plantInfos.stream()
                .map(p -> new PlantInfoListResponseDto(
                        p.getId(),
                        p.getCommonName(),
                        p.getEnglishName(),
                        p.getImageUrl()
                ))
                .toList();
    }
}
