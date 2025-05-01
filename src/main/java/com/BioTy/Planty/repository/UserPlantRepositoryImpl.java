package com.BioTy.Planty.repository;

import com.BioTy.Planty.dto.plant.PlantInfoDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.PersonalityResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailEnvStandardDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailSensorDto;
import com.BioTy.Planty.dto.userPlant.detail.DetailStatusDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserPlantRepositoryImpl implements UserPlantRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

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

        return rows.stream().map(row -> new UserPlantSummaryResponseDto(
                ((Number) row[0]).longValue(),         // userPlantId
                (String) row[1],                       // nickname
                (String) row[2],                       // imageUrl
                ((java.sql.Date) row[3]).toLocalDate(),// adoptedAt
                new UserPlantSummaryResponseDto.Status(
                        (Integer) row[4],
                        (Integer) row[5],
                        (Integer) row[6],
                        (String) row[7]
                ),
                new UserPlantSummaryResponseDto.Personality(
                        (String) row[8],
                        (String) row[9],
                        (String) row[10]
                )
        )).toList();
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
              pes.min_humidity, pes.max_humidity,
              pes.min_light, pes.max_light,

              -- PlantStatus (latest)
              ps.temperature_score, ps.light_score, ps.humidity_score, ps.status_message, ps.checked_at,

              -- SensorLogs (latest)
              sl.temperature, sl.humidity, sl.light, sl.recorded_at

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
                SELECT sl1.*
                FROM sensor_logs sl1
                ORDER BY sl1.recorded_at DESC
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
                sensorDto
        );

    }
}
