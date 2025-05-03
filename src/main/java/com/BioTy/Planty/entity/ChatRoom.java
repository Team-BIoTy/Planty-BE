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
    private LocalDateTime lastSentAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userPlantId", referencedColumnName = "id", insertable = false, updatable = false)
    private UserPlant userPlant; // 읽기 전용

    public ChatRoom(Long userId, Long userPlantId, LocalDateTime now) {
        this.userId = userId;
        this.userPlantId = userPlantId;
        this.lastSentAt = now;
    }

    public void updateLastSentAt(LocalDateTime time) {
        this.lastSentAt = time;
    }
}
