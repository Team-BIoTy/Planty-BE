package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.IotDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IotRepository extends JpaRepository<IotDevice, Long> {
    List<IotDevice> findAllByUserId(Long userId);

    @Query("SELECT d FROM IotDevice d JOIN FETCH d.user WHERE d.id = :id")
    Optional<IotDevice> findByIdWithUser(@Param("id") Long id);
}
