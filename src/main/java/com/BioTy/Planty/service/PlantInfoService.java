package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
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

    // 전체 식물 목록 조회 (식물 도감)
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

    // 식물 상세 정보 조회
    public PlantInfoDetailResponseDto getPlantInfoDetail(Long plantId){
        PlantInfo plant = plantInfoRepository.findById(plantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 식물이 존재하지 않습니다."));

        return new PlantInfoDetailResponseDto(
                plant.getId(),
                plant.getImageUrl(),

                plant.getCommonName(),
                plant.getScientificName(),
                plant.getEnglishName(),
                plant.getTradeName(),
                plant.getFamilyName(),
                plant.getOrigin(),
                plant.getCareTip(),

                plant.getCategory(),
                plant.getGrowthForm(),
                plant.getGrowthHeight(),
                plant.getGrowthWidth(),
                plant.getIndoorGardenUse(),
                plant.getEcologicalType(),
                plant.getLeafShape(),
                plant.getLeafPattern(),
                plant.getLeafColor(),

                plant.getFloweringSeason(),
                plant.getFlowerColor(),
                plant.getFruitingSeason(),
                plant.getFruitColor(),
                plant.getFragrance(),
                plant.getPropagationMethod(),
                plant.getPropagationSeason(),

                plant.getCareLevel(),
                plant.getCareDifficulty(),
                plant.getLightRequirement(),
                plant.getPlacement(),
                plant.getGrowthRate(),
                plant.getOptimalTemperature(),
                plant.getMinWinterTemperature(),
                plant.getHumidity(),
                plant.getFertilizer(),
                plant.getSoilType(),

                plant.getWateringSpring(),
                plant.getWateringSummer(),
                plant.getWateringAutumn(),
                plant.getWateringWinter(),
                plant.getPestsDiseases(),

                plant.getFunctionalInfo()
        );
    }
}
