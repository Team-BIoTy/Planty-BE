package com.BioTy.Planty.repository;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.PersonalityResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailEnvStandardDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailSensorDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailStatusDto;
import com.BioTy.Planty.entity.IotDevice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserPlantRepositoryImpl implements UserPlantRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    private final DeviceCommandRepository deviceCommandRepository;

    @Override
    public List<UserPlantSummaryResponseDto> findSummaryDtoByUserId(Long userId) {
        String sql = """
            SELECT\s
              up.id AS userPlantId,
              up.nickname,
              up.image_url AS imageUrl,
              up.adopted_at AS adoptedAt,
              ps.temperature_score,
              ps.light_score,
              ps.humidity_score,
              ps.status_message,
              ps.checked_at,
              p.label AS personality_label,
              p.emoji AS personality_emoji,
              p.color AS personality_color
            FROM user_plant up
            LEFT JOIN personality p ON up.personality_id = p.id
            LEFT JOIN plant_status ps ON ps.user_plant_id = up.id
                AND ps.checked_at = (
                    SELECT MAX(inner_ps.checked_at)
                    FROM plant_status inner_ps
                    WHERE inner_ps.user_plant_id = up.id
                )
            WHERE up.user_id = ?1
        """;

        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter(1, userId)
                .getResultList();

        List<Long> userPlantIds = rows.stream()
                .map(row -> ((Number) row[0]).longValue())
                .toList();

        List<Object[]> commandRows
                = deviceCommandRepository.findRunningCommandsByUserPlantIds(userPlantIds, LocalDateTime.now());

        Map<Long, Map<String, Long>> runningMap = new HashMap<>();
        for(Object[] row : commandRows) {
            Long plantId = ((Number) row[0]).longValue();
            String commandType = (String)row[1];
            Long commandId = ((Number) row[2]).longValue();

            runningMap
                    .computeIfAbsent(plantId, k -> new HashMap<>())
                    .put(commandType, commandId);
        }

        return rows.stream().map(row -> {
            Long userPlantId = ((Number) row[0]).longValue();
            java.sql.Timestamp checkedAtTs = null;
            try {
                checkedAtTs = (java.sql.Timestamp) row[8];
            } catch (ClassCastException e) {
                System.err.println("checkedAt 캐스팅 에러: " + row[8]);
            }
            LocalDateTime checkedAt = (checkedAtTs != null) ? checkedAtTs.toLocalDateTime() : null;

            UserPlantSummaryResponseDto dto = new UserPlantSummaryResponseDto(
                    userPlantId,
                    (String) row[1],                       // nickname
                    (String) row[2],                       // imageUrl
                    ((java.sql.Date) row[3]).toLocalDate(),// adoptedAt
                    new UserPlantSummaryResponseDto.Status(
                            (Integer) row[4],
                            (Integer) row[5],
                            (Integer) row[6],
                            (String) row[7],
                            checkedAt
                    ),
                    new UserPlantSummaryResponseDto.Personality(
                            (String) row[9],
                            (String) row[10],
                            (String) row[11]
                    )
            );

            Map<String, Long> commandMap = new HashMap<>();
            commandMap.put("FAN", null);
            commandMap.put("LIGHT", null);
            commandMap.put("WATER", null);

            if (runningMap.containsKey(userPlantId)) {
                commandMap.putAll(runningMap.get(userPlantId));
            }

            dto.setRunningCommands(commandMap);
            return dto;
        }).toList();
    }

    @Override
    public UserPlantDetailResponseDto findDetailDtoByUserPlantId(Long userPlantId) {
        String sql = """
            SELECT
              -- UserPlant
              up.id, up.nickname, up.image_url, up.adopted_at,

              -- Personality
              p.id, p.label, p.emoji, p.color, p.description, p.example_comment,

              -- PlantInfo
              pi.id, pi.image_url, pi.common_name, pi.scientific_name, pi.english_name, pi.trade_name,
              pi.family_name, pi.origin, pi.care_tip, pi.category, pi.growth_form,
              pi.growth_height, pi.growth_width, pi.indoor_garden_use, pi.ecological_type,
              pi.leaf_shape, pi.leaf_pattern, pi.leaf_color, pi.flowering_season, pi.flower_color,
              pi.fruiting_season, pi.fruit_color, pi.fragrance, pi.propagation_method, pi.propagation_season,
              pi.care_level, pi.care_difficulty, pi.light_requirement, pi.placement, pi.growth_rate,
              pi.optimal_temperature, pi.min_winter_temperature, pi.humidity, pi.fertilizer, pi.soil_type,
              pi.watering_spring, pi.watering_summer, pi.watering_autumn, pi.watering_winter,
              pi.pests_diseases, pi.functional_info,

              -- PlantEnvStandards
              pes.min_temperature, pes.max_temperature,
              pes.min_light, pes.max_light,
              pes.min_humidity, pes.max_humidity,

              -- PlantStatus (latest)
              ps.temperature_score, ps.light_score, ps.humidity_score, ps.status_message, ps.checked_at,

              -- SensorLogs (latest)
              sl.temperature, sl.humidity, sl.light, sl.recorded_at,
              
              -- IotDevice
              id.id, id.device_serial, id.model, id.status

            FROM user_plant up
            LEFT JOIN personality p ON up.personality_id = p.id
            LEFT JOIN plant_info pi ON up.plant_info_id = pi.id
            LEFT JOIN plant_env_standards pes ON pi.id = pes.plant_info_id
            LEFT JOIN iot_device id ON id.id = up.iot_device_id
            LEFT JOIN (
                SELECT ps1.*
                FROM plant_status ps1
                WHERE ps1.user_plant_id = ?1
                ORDER BY ps1.checked_at DESC
                LIMIT 1
            ) ps ON ps.user_plant_id = up.id
            LEFT JOIN (
              SELECT *
              FROM (
                SELECT sl1.*,\s
                       ROW_NUMBER() OVER (PARTITION BY sl1.device_id ORDER BY sl1.recorded_at DESC) AS rn
                FROM sensor_logs sl1
              ) filtered
              WHERE rn = 1
            ) sl ON sl.device_id = id.id
            WHERE up.id = ?1
        """;

        Object[] result = (Object[]) em.createNativeQuery(sql)
                .setParameter(1, userPlantId)
                .getSingleResult();

        int idx = 0;

        // --- 기본 정보 매핑
        Long userPlantIdRes = ((Number) result[idx++]).longValue();
        String nickname = (String) result[idx++];
        String imageUrl = (String) result[idx++];
        var adoptedAt = ((java.sql.Date) result[idx++]).toLocalDate();

        // --- Personality 매핑
        PersonalityResponseDto personality = new PersonalityResponseDto(
                ((Number) result[idx++]).longValue(),
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++]
        );

        // --- PlantInfo 매핑
        PlantInfoDetailResponseDto plantInfo = new PlantInfoDetailResponseDto(
                ((Number) result[idx++]).longValue(),
                (String) result[idx++],

                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],

                (String) result[idx++],
                (String) result[idx++],
                (Integer) result[idx++],
                (Integer) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],

                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],

                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],

                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],
                (String) result[idx++],

                (String) result[idx++]
        );

        // --- 환경 기준 매핑
        DetailEnvStandardDto envStandard = new DetailEnvStandardDto(
                new DetailEnvStandardDto.Range((Integer) result[idx++], (Integer) result[idx++]),
                new DetailEnvStandardDto.Range((Integer) result[idx++], (Integer) result[idx++]),
                new DetailEnvStandardDto.Range((Integer) result[idx++], (Integer) result[idx++])
        );

        // --- Status 매핑
        Integer tempScore = (Integer) result[idx++];
        Integer lightScore = (Integer) result[idx++];
        Integer humidityScore = (Integer) result[idx++];
        String statusMessage = (String) result[idx++];
        java.sql.Timestamp checkedAt = (java.sql.Timestamp) result[idx++];

        DetailStatusDto statusDto = (tempScore != null || lightScore != null || humidityScore != null || statusMessage != null || checkedAt != null)
                ? new DetailStatusDto(
                tempScore,
                lightScore,
                humidityScore,
                statusMessage,
                checkedAt != null ? checkedAt.toLocalDateTime() : null
        )
                : null;

        // --- Sensor 매핑
        BigDecimal sensorTemp = (BigDecimal) result[idx++];
        BigDecimal sensorHumidity = (BigDecimal) result[idx++];
        BigDecimal sensorLight = (BigDecimal) result[idx++];
        java.sql.Timestamp sensorRecordedAt = (java.sql.Timestamp) result[idx++];

        DetailSensorDto sensorDto = (sensorTemp != null || sensorHumidity != null || sensorLight != null || sensorRecordedAt != null)
                ? new DetailSensorDto(
                sensorTemp,
                sensorHumidity,
                sensorLight,
                sensorRecordedAt != null ? sensorRecordedAt.toLocalDateTime() : null
        )
                : null;

        Long deviceId = result[idx] != null ? ((Number) result[idx++]).longValue() : null;
        String deviceSerial = (String) result[idx++];
        String deviceModel = (String) result[idx++];
        String deviceStatus = (String) result[idx++];

        IotDevice iotDevice = null;
        if (deviceId != null) {
            iotDevice = new IotDevice();
            iotDevice.setId(deviceId);
            iotDevice.setDeviceSerial(deviceSerial);
            iotDevice.setModel(deviceModel);
            iotDevice.setStatus(deviceStatus);
        }

        // --- 최종 조립
        return new UserPlantDetailResponseDto(
                userPlantIdRes,
                nickname,
                imageUrl,
                adoptedAt,
                personality,
                plantInfo,
                envStandard,
                statusDto,
                sensorDto,
                iotDevice
        );

    }
}
