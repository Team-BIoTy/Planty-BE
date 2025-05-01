package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long userPlantId; // 일반 식물 챗봇일 경우 null
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatRoom(Long userId, Long userPlantId, LocalDateTime createdAt) {
        this.userId = userId;
        this.userPlantId = userPlantId;
        this.createdAt = createdAt;
    }
}
