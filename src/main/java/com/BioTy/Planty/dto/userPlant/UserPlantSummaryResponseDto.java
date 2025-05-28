package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserPlantSummaryResponseDto {
    private Long userPlantId;
    private String nickname;
    private String imageUrl;
    private LocalDate adoptedAt;

    private Status status;
    private Personality personality;

    @Getter
    @AllArgsConstructor
    public static class Status{
        private Integer temperatureScore;
        private Integer lightScore;
        private Integer humidityScore;
        private String message;
        private LocalDateTime checkedAt;
    }

    @Getter
    @AllArgsConstructor
    public static class Personality{
        private String label;
        private String emoji;
        private String color;
    }

}
