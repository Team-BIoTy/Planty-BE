package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
@Getter
@Setter
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

    @Column(nullable = false)
    private boolean autoControlEnabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personality_id", nullable = false)
    private Personality personality;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_info_id", nullable = false)
    private PlantInfo plantInfo;

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlantStatus> statuses = new ArrayList<>();

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceCommand> deviceCommands = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iot_device_id")
    private IotDevice iotDevice;

    @OneToMany(mappedBy = "userPlant", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserNotification> userNotifications = new ArrayList<>();

    public PlantStatus getLatestStatus(){
        return statuses.stream()
                .max(Comparator.comparing(PlantStatus::getCheckedAt))
                .orElse(null);
    }

    @Builder
    public UserPlant(User user, String nickname, String imageUrl, LocalDate adoptedAt,
                     Personality personality, PlantInfo plantInfo, boolean autoControlEnabled){
        this.user = user;
        this.nickname = nickname;
        this.imageUrl = imageUrl;
        this.adoptedAt = adoptedAt;
        this.personality = personality;
        this.plantInfo = plantInfo;
        this.autoControlEnabled = autoControlEnabled;
    }

    public void setIotDevice(IotDevice iotDevice){
        this.iotDevice = iotDevice;
    }
}
