package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    private Sender sender; // USER or BOT

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();

    public ChatMessage(Long chatRoomId, Sender sender, String message) {
        this.chatRoomId = chatRoomId;
        this.sender = sender;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public enum Sender{
        USER, BOT
    }
}


