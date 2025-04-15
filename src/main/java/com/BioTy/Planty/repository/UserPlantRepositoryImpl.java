package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.UserPlant;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Id;
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
    public List<UserPlant> findAllWithLatestStatusAndPersonalityByUserId(Long userId) {
        String sql = """
                SELECT up.*
                FROM user_plant up
                LEFT JOIN personality p ON up.personality_id = p.id
                LEFT JOIN plant_status ps ON ps.user_plant_id = up.id
                WHERE up.user_id = ?1
                    AND ps.checked_at = (
                        SELECT MAX(inner_ps.checked_at)
                        FROM plant_status inner_ps
                        WHERE inner_ps.user_plant_id = up.id
                    )
                """;

        List<UserPlant> result = em.createNativeQuery(sql, UserPlant.class)
                .setParameter(1, userId)
                .getResultList();

        return result;
    }
}
