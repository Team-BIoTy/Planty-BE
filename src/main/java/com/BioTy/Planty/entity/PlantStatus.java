package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlantStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_plant_id", nullable = false)
    private UserPlant userPlant;

    private Integer temperatureScore;
    private Integer lightScore;
    private Integer humidityScore;

    private String statusMessage;
    private LocalDateTime checkedAt;

    public PlantStatus(UserPlant userPlant, Integer temperatureScore, Integer humidityScore,
                       Integer lightScore, String statusMessage, LocalDateTime checkedAt) {
        this.userPlant = userPlant;
        this.temperatureScore = temperatureScore;
        this.lightScore = lightScore;
        this.humidityScore = humidityScore;
        this.statusMessage = statusMessage;
        this.checkedAt = checkedAt;
    }
}
