package com.BioTy.Planty.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class SoilMoisture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String feedId;
    private Double value;
    private LocalDateTime sensorCreatedAt;
    private LocalDateTime sensorUpdatedAt;

    @CreationTimestamp
    private LocalDateTime savedAt;

}
