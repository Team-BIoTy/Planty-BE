package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPlant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String nickname;
    private String imageUrl;
    private LocalDate adoptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_id", nullable = false)
    private Personality personality;

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantStatus> statuses = new ArrayList<>();

    public PlantStatus getLatestStatus(){
        return statuses.stream()
                .max(Comparator.comparing(PlantStatus::getCheckedAt))
                .orElse(null);
    }

    public UserPlant(User user, String nickname, String imageUrl, LocalDate adoptedAt, Personality personality){
        this.user = user;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.adoptedAt = adoptedAt;
        this.personality = personality;
    }
}
