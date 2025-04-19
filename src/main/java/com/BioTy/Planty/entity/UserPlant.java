package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_info_id", nullable = false)
    private PlantInfo plantInfo;

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantStatus> statuses = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iot_device_id")
    private IotDevice iotDevice;

    public PlantStatus getLatestStatus(){
        return statuses.stream()
                .max(Comparator.comparing(PlantStatus::getCheckedAt))
                .orElse(null);
    }

    @Builder
    public UserPlant(User user, String nickname, String imageUrl, LocalDate adoptedAt, Personality personality, PlantInfo plantInfo){
        this.user = user;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.adoptedAt = adoptedAt;
        this.personality = personality;
        this.plantInfo = plantInfo;
    }

    public void setIotDevice(IotDevice iotDevice){
        this.iotDevice = iotDevice;
    }
}
