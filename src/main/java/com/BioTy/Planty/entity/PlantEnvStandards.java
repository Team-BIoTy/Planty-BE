package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantEnvStandards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 식물의 기준인지
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_info_id", nullable = false)
    private PlantInfo plantInfo;

    private Integer minTemperature;
    private Integer maxTemperature;

    private Integer minHumidity;
    private Integer maxHumidity;

    private Integer minLight;
    private Integer maxLight;
}
