package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.DeviceActionLog;
import com.BioTy.Planty.entity.DeviceCommand;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.DeviceActionLogRepository;
import com.BioTy.Planty.repository.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {
    private final IotService iotService;
    private final DeviceCommandRepository commandRepository;
    private final DeviceActionLogRepository actionLogRepository;

    public void executeCommands(UserPlant userPlant, List<String> actionTypes) {
        for (String action : actionTypes) {
            try {
                // 1. 명령 전송
                iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), action);

                // 2. 전송한 명령 기록 (device_command)
                DeviceCommand command = DeviceCommand.builder()
                        .iotDevice(userPlant.getIotDevice())
                        .userPlant(userPlant)
                        .commandType(action)
                        .sentAt(LocalDateTime.now())
                        .build();
                commandRepository.save(command);

                // 3. 실행 후 상태 기록 (device_action_log)
                DeviceActionLog log = DeviceActionLog.builder()
                        .command(command)
                        .executedAt(LocalDateTime.now())
                        .result("pending")
                        .build();
                actionLogRepository.save(log);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
