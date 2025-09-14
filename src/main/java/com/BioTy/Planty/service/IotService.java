package com.BioTy.Planty.service;

import com.BioTy.Planty.config.AdafruitClient;
import com.BioTy.Planty.dto.iot.IotDeviceResponseDto;
import com.BioTy.Planty.dto.iot.SensorLogResponseDto;
import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.entity.SensorLogs;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.IotRepository;
import com.BioTy.Planty.repository.SensorLogsRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import com.BioTy.Planty.security.EncryptionUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class IotService {
    private final IotRepository iotRepository;
    private final UserPlantRepository userPlantRepository;
    private final AdafruitClient adafruitClient;
    private final SensorLogsRepository sensorLogsRepository;
    private final EncryptionUtil encryptionUtil;

    public List<IotDeviceResponseDto> getDevicesByUserId(Long userId){
        return iotRepository.findAllByUserId(userId).stream()
                .map(IotDeviceResponseDto::from)
                .toList();
    }

    @Transactional
    public void fetchAndSaveSensorLog(Long deviceId) {
        IotDevice device = iotRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("기기를 찾을 수 없습니다."));

        String username = device.getUser().getAdafruitUsername();
        String apiKey = encryptionUtil.decrypt(device.getUser().getAdafruitApiKey());

        String lightKey = "planty.lightintensity";
        String tempKey = "planty.temperature";
        String humidKey = "planty.soilmoisture";

        SensorLogResponseDto light = adafruitClient.fetchFeedInfo(username, apiKey, lightKey);
        SensorLogResponseDto temperature = adafruitClient.fetchFeedInfo(username, apiKey, tempKey);
        SensorLogResponseDto humidity = adafruitClient.fetchFeedInfo(username, apiKey, humidKey);

        BigDecimal lightVal = new BigDecimal(light.getLastValue());
        BigDecimal temperatureVal = new BigDecimal(temperature.getLastValue());
        BigDecimal humidityVal = new BigDecimal(humidity.getLastValue());


        // 가장 최신 updatedAt 기준으로 기록 시간 저장
        LocalDateTime recordedAt = Stream.of(light, temperature, humidity)
                .map(f -> ZonedDateTime.parse(f.getUpdatedAt()))
                .max(Comparator.naturalOrder())
                .get()
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        SensorLogs logs = new SensorLogs(device, temperatureVal, humidityVal, lightVal,
                recordedAt, LocalDateTime.now());
        sensorLogsRepository.save(logs);
    }

    public void sendCommandToAdafruit(Long userPlantId, Long userId, String actionType) {
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new RuntimeException("반려식물이 존재하지 않습니다."));

        if (!userPlant.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 식물에 대한 권한이 없습니다.");
        }

        IotDevice device = userPlant.getIotDevice();
        String username = userPlant.getUser().getAdafruitUsername();
        String apiKey = encryptionUtil.decrypt(userPlant.getUser().getAdafruitApiKey());

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        switch (actionType.toUpperCase()) {
            case "WATER_ON" -> adafruitClient.sendCommand(username, apiKey, "action.water", "ON");
            case "WATER_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.water", "OFF");

            case "FAN_ON" -> adafruitClient.sendCommand(username, apiKey, "action.fan", "ON");
            case "FAN_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.fan", "OFF");

            case "LIGHT_ON" -> adafruitClient.sendCommand(username, apiKey, "action.light", "ON");
            case "LIGHT_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.light", "OFF");

            case "REFRESH" -> {
                String value = device.getId() + "_" + timestamp;
                adafruitClient.sendCommand(username, apiKey, "action.refresh", value);
                try {
                    Thread.sleep(3000); // 3초 정도 대기
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                fetchAndSaveSensorLog(device.getId());
            }
            default -> throw new IllegalArgumentException("지원하지 않는 명령입니다." + actionType);
        }
    }

    public void sendCommandToAdafruit(
            String actionType, String username, String apiKey, Long deviceId
    ) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        switch (actionType.toUpperCase()) {
            case "WATER_ON" -> adafruitClient.sendCommand(username, apiKey, "action.water", "ON");
            case "WATER_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.water", "OFF");

            case "FAN_ON" -> adafruitClient.sendCommand(username, apiKey, "action.fan", "ON");
            case "FAN_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.fan", "OFF");

            case "LIGHT_ON" -> adafruitClient.sendCommand(username, apiKey, "action.light", "ON");
            case "LIGHT_OFF" -> adafruitClient.sendCommand(username, apiKey, "action.light", "OFF");

            case "REFRESH" -> {
                String value = deviceId + "_" + timestamp;
                adafruitClient.sendCommand(username, apiKey, "action.refresh", value);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                fetchAndSaveSensorLog(deviceId);
            }

            default -> throw new IllegalArgumentException("지원하지 않는 명령입니다." + actionType);
        }
    }

}
