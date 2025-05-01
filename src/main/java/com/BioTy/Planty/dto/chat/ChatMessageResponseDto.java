package com.BioTy.Planty.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String sender; // USER or BOT
    private String message;
    private LocalDateTime timestamp;
}
