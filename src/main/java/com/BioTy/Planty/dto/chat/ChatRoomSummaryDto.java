package com.BioTy.Planty.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomSummaryDto {
    private Long chatRoomId;
    private String userPlantNickname;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private String imageUrl;

    public ChatRoomSummaryDto(Long chatRoomId, String nickname, String lastMsg, LocalDateTime lastTime, String imageUrl) {
        this.chatRoomId = chatRoomId;
        this.userPlantNickname = nickname;
        this.lastMessage = lastMsg;
        this.lastSentAt = lastTime;
        this.imageUrl = imageUrl;
    }
}
