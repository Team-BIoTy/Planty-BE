package com.BioTy.Planty.config;

import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.SensorLogsRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import com.BioTy.Planty.service.DeviceCommandService;
import com.BioTy.Planty.service.IotService;
import com.BioTy.Planty.service.PlantStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorFetchScheduler {
    private final IotService iotService;
    private final PlantStatusService plantStatusService;
    private final DeviceCommandService deviceCommandService;
    private final UserPlantRepository userPlantRepository;

    @Scheduled(cron = "0 0 * * * *") // 매시 정각마다
    public void fetchSensorDataEveryHour() {
        List<UserPlant> allPlants = userPlantRepository.findAll();

        for (UserPlant plant : allPlants) {
            Long deviceId = plant.getIotDevice().getId();

            // 1. 센서 데이터 패치
            iotService.fetchAndSaveSensorLog(deviceId);

            // 2. 상태 평가 (자동제어 설정 + 쿨타임 확인)
            List<String> actions = plantStatusService.evaluatePlantStatus(deviceId);

            // 3. 자동제어 조건이면 실행
            if (!actions.isEmpty()) {
                deviceCommandService.executeCommands(plant, actions);
            }
        }
    }
}
