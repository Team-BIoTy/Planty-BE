package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.PlantEnvStandards;
import com.BioTy.Planty.entity.PlantStatus;
import com.BioTy.Planty.entity.SensorLogs;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.PlantEnvStandardsRepository;
import com.BioTy.Planty.repository.PlantStatusRepository;
import com.BioTy.Planty.repository.SensorLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PlantStatusService {
    private final SensorLogsRepository sensorLogsRepository;
    private final PlantEnvStandardsRepository plantEnvStandardsRepository;
    private final PlantStatusRepository plantStatusRepository;

    public void evaluatePlantStatus(Long deviceId){
        SensorLogs latestLog = sensorLogsRepository.findTopByIotDevice_IdOrderByRecordedAtDesc(deviceId)
                .orElseThrow(() -> new RuntimeException("센서 로그 없음"));
        UserPlant userPlant = latestLog.getIotDevice().getUserPlant();
        PlantEnvStandards standards = plantEnvStandardsRepository.findByPlantInfo(userPlant.getPlantInfo())
                .orElseThrow(() -> new RuntimeException("환경 기준 없음"));

        int temperatureScore = calcScore(latestLog.getTemperature(), standards.getMinTemperature(), standards.getMaxTemperature());
        int lightScore = calcScore(latestLog.getLight(), standards.getMinLight(), standards.getMaxLight());
        int humidityScore = calcScore(latestLog.getHumidity(), standards.getMinHumidity(), standards.getMaxHumidity());

        String msg = createStatusMessage(temperatureScore, lightScore, humidityScore);
        PlantStatus status = PlantStatus.builder()
                .userPlant(userPlant)
                .temperatureScore(temperatureScore)
                .lightScore(lightScore)
                .humidityScore(humidityScore)
                .statusMessage(msg)
                .checkedAt(LocalDateTime.now())
                .build();

        plantStatusRepository.save(status);
    }

    private int calcScore(BigDecimal value, int min, int max){
        return (value.compareTo(BigDecimal.valueOf(min)) >= 0 &&
                value.compareTo(BigDecimal.valueOf(max)) <= 0) ? 1 : 0;
    }

    // 임시 메시지 (추후 AI 연동 - 성격에 맞게 변경)
    private String createStatusMessage(int temp, int humid, int light) {
        if (temp + humid + light == 3) return "모든 환경이 아주 좋아요!";
        if (humid == 0) return "습도가 너무 낮아요. 물이 필요해요!";
        if (light == 0) return "빛이 부족해요. 햇볕이 필요해요!";
        if (temp == 0) return "온도가 적절하지 않아요!";
        return "식물의 상태를 다시 확인해주세요.";
    }
}
