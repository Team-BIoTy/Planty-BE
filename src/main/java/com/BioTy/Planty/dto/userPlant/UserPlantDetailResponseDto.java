package com.BioTy.Planty.dto.userPlant;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailEnvStandardDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailSensorDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailStatusDto;
import com.BioTy.Planty.entity.IotDevice;
import lombok.Getter;

import java.time.LocalDate;

@Getter
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
    private IotDevice iotDevice;

    public UserPlantDetailResponseDto(long id, String nickname, String imageUrl, LocalDate adoptedAt,
                                      PersonalityResponseDto personality, PlantInfoDetailResponseDto plantInfo,
                                      DetailEnvStandardDto envStandard, DetailStatusDto status,
                                      DetailSensorDto sensorData, IotDevice iotDevice) {
        this.id = id;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.adoptedAt = adoptedAt;
        this.personality = personality;
        this.plantInfo = plantInfo;
        this.envStandard = envStandard;
        this.status = status;
        this.sensorData = sensorData;
        this.iotDevice = iotDevice;
    }

}
