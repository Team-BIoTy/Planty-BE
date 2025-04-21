package com.BioTy.Planty.repository;

import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
