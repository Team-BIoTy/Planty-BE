package com.BioTy.Planty.dto.chat;

import com.BioTy.Planty.entity.PlantInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private Long userPlantId;
    private String userPlantNickname;
    private String imageUrl;
    private String personalityLabel;
    private String personalityEmoji;
    private String personalityColor;
    private Long sensorLogId;
    private Long plantEnvStandardsId;
    private List<ChatMessageResponseDto> messages;
    private PlantInfo plantInfo;
}
