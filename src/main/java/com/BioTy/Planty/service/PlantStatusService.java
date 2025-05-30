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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlantStatusService {
    private final SensorLogsRepository sensorLogsRepository;
    private final PlantEnvStandardsRepository plantEnvStandardsRepository;
    private final PlantStatusRepository plantStatusRepository;
    private final DeviceCommandService deviceCommandService;

    @Transactional
    public void evaluatePlantStatus(Long deviceId){
        // 1. 최신 센서 로그 조회 & 반려식물과 환경기준 조회
        SensorLogs latestLog = sensorLogsRepository.findTopByIotDevice_IdOrderByRecordedAtDesc(deviceId)
                .orElseThrow(() -> new RuntimeException("센서 로그 없음"));
        UserPlant userPlant = latestLog.getIotDevice().getUserPlant();
        PlantEnvStandards standards = plantEnvStandardsRepository.findByPlantInfo(userPlant.getPlantInfo())
                .orElseThrow(() -> new RuntimeException("환경 기준 없음"));

        // 2. 센서값과 환경 기준 비교해 점수 계산 & 상태메시지 생성
        int temperatureScore = calcScore(latestLog.getTemperature(), standards.getMinTemperature(), standards.getMaxTemperature());
        int lightScore = calcScore(latestLog.getLight(), standards.getMinLight(), standards.getMaxLight());
        int humidityScore = calcScore(latestLog.getHumidity(), standards.getMinHumidity(), standards.getMaxHumidity());
        String msg = createStatusMessage(temperatureScore, lightScore, humidityScore);

        // 3. 명령 판단
        List<String> actionTypes = determinAction(temperatureScore, lightScore, humidityScore);
        boolean actionNeeded = !actionTypes.isEmpty();

        // 4. 자동제어 여부에 따라 분기 처리
        if (actionNeeded && userPlant.isAutoControlEnabled()) {
            deviceCommandService.executeCommands(userPlant, actionTypes);
        }

        // 5. 상태 저장 (actionNeeded는 자동제어 OFF이고 기준 미달일 때만 true)
        PlantStatus status = PlantStatus.builder()
                .userPlant(userPlant)
                .temperatureScore(temperatureScore)
                .lightScore(lightScore)
                .humidityScore(humidityScore)
                .statusMessage(msg)
                .checkedAt(LocalDateTime.now())
                .actionNeeded(!userPlant.isAutoControlEnabled() && actionNeeded)
                .build();

        plantStatusRepository.save(status);
    }

    private int calcScore(BigDecimal value, int min, int max){
        return (value.compareTo(BigDecimal.valueOf(min)) >= 0 &&
                value.compareTo(BigDecimal.valueOf(max)) <= 0) ? 1 : 0;
    }

    // 임시 메시지 (추후 AI 연동 - 성격에 맞게 변경)
    private String createStatusMessage(int temp, int light, int humid) {
        if (temp + light + humid == 3) return "모든 환경이 아주 좋아요!";
        if (temp == 0) return "온도가 적절하지 않아요!";
        if (light == 0) return "빛이 부족해요. 햇볕이 필요해요!";
        if (humid == 0) return "습도가 너무 낮아요. 물이 필요해요!";
        return "식물의 상태를 다시 확인해주세요.";
    }

    private List<String> determinAction(int tempScore, int lightScore, int humidScore){
        List<String> actions = new ArrayList<>();
        if (tempScore == 0) actions.add("FAN");
        if (lightScore == 0) actions.add("LIGHT");
        if (humidScore == 0) actions.add("WATER");
        return actions;
    }
}
