package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
import com.BioTy.Planty.dto.plant.PlantInfoListResponseDto;
import com.BioTy.Planty.service.PlantInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "PlantInfo", description = "식물 도감 관련 API")
@RestController
@RequestMapping("/plants")
@RequiredArgsConstructor
public class PlantInfoController {
    private final PlantInfoService plantInfoService;

    @Operation(summary = "전체 식물 도감 목록 조회", description = "모든 식물 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<PlantInfoListResponseDto>> getAllPlants(){
        List<PlantInfoListResponseDto> response = plantInfoService.getAllPlantInfoLists();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "도감 식물 상세 정보 조회", description = "선택한 식물의 상세 정보를 조회합니다.")
    @GetMapping("/{plantId}")
    public PlantInfoDetailResponseDto getPlantDetail(@PathVariable("plantId") Long plantId) {
        return plantInfoService.getPlantInfoDetail(plantId);
    }
}
