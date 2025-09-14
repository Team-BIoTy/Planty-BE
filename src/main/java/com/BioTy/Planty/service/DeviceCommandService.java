package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.DeviceCommand;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.DeviceCommandRepository;
import com.BioTy.Planty.security.EncryptionUtil;
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
    private final EncryptionUtil encryptionUtil;

    public void executeCommands(UserPlant userPlant, List<String> actionTypes) {
        Long userPlantId = userPlant.getId();
        Long userId = userPlant.getUser().getId();
        String username = userPlant.getUser().getAdafruitUsername();
        String apiKey = encryptionUtil.decrypt(userPlant.getUser().getAdafruitApiKey());
        Long deviceId = userPlant.getIotDevice().getId();

        for (String actionType : actionTypes) {
            try {
                String action = actionType.toUpperCase();

                // REFRESH 바로 실행
                if (action.equals("REFRESH")) {
                    iotService.sendCommandToAdafruit("REFRESH", username, apiKey, deviceId);
                    continue;
                }

                // 1. 명령 전송
                String onAction = action + "_ON";
                iotService.sendCommandToAdafruit(onAction, username, apiKey, deviceId);

                // 2. 지속시간 계산
                long duration = getDurationSeconds(action);
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime willEndAt = now.plusSeconds(duration);

                // 3. 명령 기록
                DeviceCommand command = DeviceCommand.builder()
                        .iotDevice(userPlant.getIotDevice())
                        .userPlant(userPlant)
                        .commandType(action)
                        .sentAt(now)
                        .status("RUNNING")
                        .willBeTurnedOffAt(willEndAt)
                        .build();
                commandRepository.save(command);

                // 4. OFF + REFRESH 예약
                scheduleOffAndRefresh(command, username, apiKey, deviceId, duration);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long getDurationSeconds(String action) {
        return switch (action) {
            case "WATER" -> 6;
            case "FAN" -> 10;
            case "LIGHT" -> 10;
            default -> 0;
        };
    }

    private void scheduleOffAndRefresh(
            DeviceCommand command, String username, String apiKey, Long deviceId,
            long delaySeconds
    ) {
        String offAction = command.getCommandType() + "_OFF";

        commandScheduler.schedule(() -> {
            try {
                // OFF
                iotService.sendCommandToAdafruit(offAction, username, apiKey, deviceId);

                // 상태 DONE 업데이트
                DeviceCommand latest = commandRepository.findById(command.getId()).orElseThrow();
                latest.setStatus("DONE");
                commandRepository.save(latest);

                // REFRESH 예약 (10초 뒤)
                commandScheduler.schedule(() -> {
                    try {
                        iotService.sendCommandToAdafruit("REFRESH", username, apiKey, deviceId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, 10, TimeUnit.SECONDS);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
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
