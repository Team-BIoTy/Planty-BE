package com.BioTy.Planty.config;

import com.BioTy.Planty.repository.SensorLogsRepository;
import com.BioTy.Planty.service.IotService;
import com.BioTy.Planty.service.PlantStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorFetchScheduler {
    private final IotService iotService;
    private final PlantStatusService plantStatusService;

    @Scheduled(cron = "0 0 * * * *") // 매시 정각마다
    public void fetchSensorDataEveryHour() {
        Long deviceId = 1L;
        iotService.fetchAndSaveSensorLog(deviceId);
        plantStatusService.evaluatePlantStatus(deviceId);
    }
}
