package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.DeviceCommand;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {
    private final IotService iotService;
    private final DeviceCommandRepository commandRepository;
    private final ScheduledExecutorService commandScheduler;

    public void executeCommands(UserPlant userPlant, List<String> actionTypes) {
        for (String actionType : actionTypes) {
            try {
                String action = actionType.toUpperCase();
                String onAction = action + "_ON";
                String offAction = action + "_OFF";

                // 1. 명령 전송
                iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), onAction);

                // 2. 명령 지속시간 계산
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime willEndAt = switch (action) {
                    case "WATER" -> now.plusSeconds(30);
                    case "FAN" -> now.plusMinutes(5);
                    case "LIGHT" -> now.plusMinutes(5);
                    default -> now.plusSeconds(0);
                };

                // 3. 전송한 명령 기록 (device_command)
                DeviceCommand command = DeviceCommand.builder()
                        .iotDevice(userPlant.getIotDevice())
                        .userPlant(userPlant)
                        .commandType(action)
                        .sentAt(now)
                        .status("RUNNING")
                        .willBeTurnedOffAt(willEndAt)
                        .build();
                commandRepository.save(command);

                // 4. 지속시간 후 OFF
                long delaySeconds = switch (action) {
                    case "WATER" -> 30;
                    case "FAN" -> 300;
                    case "LIGHT" -> 300;
                    default -> 0;
                };

                commandScheduler.schedule(() -> {
                    try {
                        iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), offAction);
                        command.setStatus("DONE");
                        commandRepository.save(command);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delaySeconds, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
