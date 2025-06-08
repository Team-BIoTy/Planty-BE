package com.BioTy.Planty.config;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.UserDeviceTokenRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import com.BioTy.Planty.service.DeviceCommandService;
import com.BioTy.Planty.service.FCMService;
import com.BioTy.Planty.service.IotService;
import com.BioTy.Planty.service.PlantStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SensorFetchScheduler {
    private final IotService iotService;
    private final FCMService fcmService;
    private final PlantStatusService plantStatusService;
    private final DeviceCommandService deviceCommandService;
    private final UserPlantRepository userPlantRepository;
    private final UserDeviceTokenRepository userDeviceTokenRepository;

    @Scheduled(cron = "0 0 * * * *") // 매시 정각마다
    public void fetchSensorDataEveryHour() {
        List<UserPlant> allPlants = userPlantRepository.findAll();

        for (UserPlant plant : allPlants) {
            if (plant.getIotDevice() == null) continue;
            Long deviceId = plant.getIotDevice().getId();
            User user = plant.getUser();

            // 1. 센서 데이터 패치
            iotService.fetchAndSaveSensorLog(deviceId);

            // 2. 상태 평가 (자동제어 설정 + 쿨타임 확인)
            List<String> actions = plantStatusService.evaluatePlantStatus(deviceId);
            String statusMsg = plantStatusService.getLastStatusMessage(plant.getId());

            // 3. 수동제어 - 상태 메시지 푸시 전송
            if (actions.isEmpty()) {
                userDeviceTokenRepository.findByUser(plant.getUser()).ifPresent(token -> {
                    log.info("🔥 FCM 발송 → {}", statusMsg);
                    fcmService.sendMessage(
                            user,
                            plant,
                            token.getToken(),
                            "🌱식물 상태 알림",
                            statusMsg
                    );
                });
            }


            // 4. 자동제어 - 액션 수행 & 상태 메시지, 액션 푸시 전송
            if (!actions.isEmpty()) {
                deviceCommandService.executeCommands(plant, actions);
                userDeviceTokenRepository.findByUser(plant.getUser()).ifPresent(token -> {
                    fcmService.sendMessage(
                            user,
                            plant,
                            token.getToken(),
                            "🌱자동제어가 작동했어요!",
                            statusMsg
                    );
                });
            }
        }
    }
}
