package com.BioTy.Planty.dto.userPlant;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailEnvStandardDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailSensorDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailStatusDto;

import java.time.LocalDate;

public class UserPlantDetailResponseDto {
    private Long id; // userPlant id
    private String nickname;
    private String imageUrl;
    private LocalDate adoptedAt;

    private PersonalityResponseDto personality; // 성격
    private PlantInfoDetailResponseDto plantInfo; // 도감 정보
    private DetailEnvStandardDto envStandard; // 환경 기준 (min, max값)
    private DetailSensorDto sensorData; // 최신 센서 데이터
    private DetailStatusDto status; // 최신 상태 (0~3점, 상태 메시지)
}
