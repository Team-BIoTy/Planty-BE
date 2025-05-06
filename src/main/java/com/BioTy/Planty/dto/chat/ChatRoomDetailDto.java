package com.BioTy.Planty.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetailDto {
    private Long chatRoomId;
    private String userPlantNickname;
    private String imageUrl;
    private String personalityLabel;
    private String personalityEmoji;
    private String personalityColor;
    private List<ChatMessageResponseDto> messages;
}
