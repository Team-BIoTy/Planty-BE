package com.BioTy.Planty.dto.notification;

import com.BioTy.Planty.entity.UserNotification;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDto {
    private Long id;
    private String title;
    private String body;
    private boolean read;
    private LocalDateTime receivedAt;

    private Long userPlantId;
    private String plantName;
    private String plantImageUrl;

    public static NotificationResponseDto from(UserNotification entity) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setBody(entity.getBody());
        dto.setRead(entity.isRead());
        dto.setReceivedAt(entity.getReceivedAt());

        if (entity.getUserPlant() != null) {
            dto.setUserPlantId(entity.getUserPlant().getId());
            dto.setPlantName(entity.getUserPlant().getNickname());
            dto.setPlantImageUrl(entity.getUserPlant().getImageUrl());
        }

        return dto;
    }
}
