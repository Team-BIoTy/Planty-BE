package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.environment.EnvironmentRangeDto;
import com.BioTy.Planty.dto.environment.EnvironmentReportDto;
import com.BioTy.Planty.dto.environment.Range;
import com.BioTy.Planty.dto.environment.SensorDataPoint;
import com.BioTy.Planty.entity.PlantEnvStandards;
import com.BioTy.Planty.entity.PlantInfo;
import com.BioTy.Planty.entity.SensorLogs;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.SensorLogsRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvironmentReportService {
    private final SensorLogsRepository sensorLogsRepository;
    private  final UserPlantRepository userPlantRepository;

    public EnvironmentReportDto getReport(Long userPlantId, LocalDate date) {
        // UserPlant 조회
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new RuntimeException("UserPlant를 찾을 수 없습니다."));

        Long deviceId = userPlant.getIotDevice().getId();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // 센서 로그 조회
        List<SensorLogs> logs = sensorLogsRepository
                .findByIotDevice_IdAndRecordedAtBetweenOrderByRecordedAtAsc(deviceId, start, end);

        // 시간별 센서값 리스트 만들기
        List<SensorDataPoint> temperature = logs.stream()
                .map(log -> new SensorDataPoint(
                        String.format("%02d", log.getRecordedAt().getHour()),
                        log.getTemperature().doubleValue()
                ))
                .toList();

        List<SensorDataPoint> light = logs.stream()
                .map(log -> new SensorDataPoint(
                        String.format("%02d", log.getRecordedAt().getHour()),
                        log.getLight().doubleValue()
                ))
                .toList();

        List<SensorDataPoint> humidity = logs.stream()
                .map(log -> new SensorDataPoint(
                        String.format("%02d", log.getRecordedAt().getHour()),
                        log.getHumidity().doubleValue()
                ))
                .toList();


        // 권장 환경 범위
        PlantInfo plantInfo = userPlant.getPlantInfo();
        PlantEnvStandards env = plantInfo.getPlantEnvStandards();
        EnvironmentRangeDto recommended = new EnvironmentRangeDto(
                new Range(env.getMinTemperature(), env.getMaxTemperature()),
                new Range(env.getMinLight(), env.getMaxLight()),
                new Range(env.getMinHumidity(), env.getMaxHumidity())
        );

        return new EnvironmentReportDto(
                userPlant.getNickname(),
                userPlant.isAutoControlEnabled(),
                temperature,
                light,
                humidity,
                recommended
        );
    }
}
