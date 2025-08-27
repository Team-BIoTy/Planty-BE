package com.BioTy.Planty.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "plant_info")
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;

    // 기본 정보
    private String commonName;
    private String scientificName;
    private String englishName;
    private String tradeName;
    private String familyName;
    private String origin;
    private String careTip;

    // 상세 정보
    private String category;
    private String growthForm;
    private Integer growthHeight;
    private Integer growthWidth;
    private String indoorGardenUse;
    private String ecologicalType;
    private String leafShape;
    private String leafPattern;
    private String leafColor;

    private String floweringSeason;
    private String flowerColor;
    private String fruitingSeason;
    private String fruitColor;
    private String fragrance;
    private String propagationMethod;
    private String propagationSeason;

    // 관리 정보
    private String careLevel;
    private String careDifficulty;
    private String lightRequirement;
    private String placement;
    private String growthRate;
    private String optimalTemperature;
    private String minWinterTemperature;
    private String humidity;
    private String fertilizer;
    private String soilType;

    private String wateringSpring;
    private String wateringSummer;
    private String wateringAutumn;
    private String wateringWinter;
    private String pestsDiseases;

    // 기능성 정보
    private String functionalInfo;

    @OneToOne(mappedBy = "plantInfo", fetch = FetchType.LAZY)
    @JsonIgnore
    private PlantEnvStandards plantEnvStandards;

    public PlantEnvStandards getPlantEnvStandards() {
        return this.plantEnvStandards;
    }
}