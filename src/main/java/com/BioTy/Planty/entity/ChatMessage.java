package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long chatRoomId;

    @Enumerated(EnumType.STRING)
    private Sender sender; // USER or BOT

    private String message;
    private LocalDateTime timeStamp = LocalDateTime.now();

    public enum Sender{
        USER, BOT
    }
}


