package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_device_token", uniqueConstraints = {
        @UniqueConstraint(columnNames = "userId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){ // 엔티티가 처음 저장되기 직전에 실행
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate(){ // 엔티티가 업데이트되기 직전에 실행
        this.updatedAt = LocalDateTime.now();
    }
}
