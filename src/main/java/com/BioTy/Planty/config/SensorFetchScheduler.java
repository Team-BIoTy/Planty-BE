package com.BioTy.Planty.config;

import com.BioTy.Planty.service.IotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SensorFetchScheduler {
    private final IotService iotService;

    @Scheduled(cron = "0 0 * * * *") // 매시 정각마다
    public void fetchSensorDataEveryHour() {
        Long deviceId = 1L;
        iotService.fetchAndSaveSensorLog(deviceId);
    }
}
