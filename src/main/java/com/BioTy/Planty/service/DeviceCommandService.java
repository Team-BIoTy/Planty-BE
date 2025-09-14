package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.DeviceCommand;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.DeviceCommandRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {
    private final IotService iotService;
    private final DeviceCommandRepository commandRepository;
    private final ScheduledExecutorService commandScheduler;
    private final PlantStatusService plantStatusService;

    public void executeCommands(UserPlant userPlant, List<String> actionTypes) {
        for (String actionType : actionTypes) {
            try {
                String action = actionType.toUpperCase();
                if (action.equals("REFRESH")) {
                    iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), "REFRESH");
                    continue;
                }
                String onAction = action + "_ON";
                String offAction = action + "_OFF";

                // 1. 명령 전송
                iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), onAction);

                // 2. 명령 지속시간 계산
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime willEndAt = switch (action) {
                    case "WATER" -> now.plusSeconds(3);
                    case "FAN" -> now.plusSeconds(10);
                    case "LIGHT" -> now.plusSeconds(10);
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
                    case "WATER" -> 3;
                    case "FAN" -> 10;
                    case "LIGHT" -> 10;
                    default -> 0;
                };

                commandScheduler.schedule(() -> {
                    try {
                        // OFF 명령 전송
                        iotService.sendCommandToAdafruit(userPlant.getId(), userPlant.getUser().getId(), offAction);
                        // 상태 변경
                        command.setStatus("DONE");
                        commandRepository.save(command);
                        // OFF 이후 10초 뒤 센서 재수집 & 상태 재평가
                        commandScheduler.schedule(() -> {
                            try {
                                iotService.sendCommandToAdafruit(
                                        userPlant.getId(),
                                        userPlant.getUser().getId(),
                                        "REFRESH"
                                );
                                iotService.fetchAndSaveSensorLog(userPlant.getIotDevice().getId());
                                plantStatusService.evaluatePlantStatus(userPlant.getIotDevice().getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }, 10, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, delaySeconds, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Transactional
    public void cancelCommand(Long commandId, Long userId) {
        DeviceCommand command = commandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("명령을 찾을 수 없습니다."));

        if (!command.getUserPlant().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 명령만 취소할 수 있습니다.");
        }

        if (!command.getStatus().equals("RUNNING")) {
            throw new IllegalStateException("이미 종료된 명령입니다.");
        }

        // OFF 명령 전송
        String offAction = command.getCommandType() + "_OFF";
        iotService.sendCommandToAdafruit(
                command.getUserPlant().getId(),
                userId,
                offAction
        );

        command.setStatus("CANCELLED");
        commandRepository.save(command);
    }

    public Optional<DeviceCommand> getLastCommand(Long userPlantId) {
        return commandRepository.findTopByUserPlant_IdOrderByWillBeTurnedOffAtDesc(userPlantId);
    }

}
