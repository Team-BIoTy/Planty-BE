package com.BioTy.Planty.dto.chatbot;

import java.time.LocalDateTime;

public class ChatMessageResponseDto {
    private String sender; // USER or BOT
    private String message;
    private LocalDateTime timestamp;
}
