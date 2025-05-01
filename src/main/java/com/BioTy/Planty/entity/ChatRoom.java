package com.BioTy.Planty.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long userPlantId; // 일반 식물 챗봇일 경우 null
    private LocalDateTime createdAt = LocalDateTime.now();
}
