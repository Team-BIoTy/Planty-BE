package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
public class UserPlantSummaryResponseDto {
    private Long userPlantId;
    private String nickname;
    private String imageUrl;
    private LocalDate adoptedAt;

    private Status status;
    private Personality personality;
    private Map<String, Long> runningCommands;

    public UserPlantSummaryResponseDto(
            Long userPlantId,
            String nickname,
            String imageUrl,
            LocalDate adoptedAt,
            Status status,
            Personality personality
    ) {
        this.userPlantId = userPlantId;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.adoptedAt = adoptedAt;
        this.status = status;
        this.personality = personality;
        this.runningCommands = null;
    }

    public void setRunningCommands(Map<String, Long> runningCommands) {
        this.runningCommands = runningCommands;
    }

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
