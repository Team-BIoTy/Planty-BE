package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "iot_device")
@Getter
@Setter
@NoArgsConstructor()
public class IotDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceSerial;
    private String model;
    private String status;
    private String feedKey;

    public void setFeedKey(String feedKey) {
        this.feedKey = feedKey;
    }

    @OneToOne(mappedBy = "iotDevice")
    private UserPlant userPlant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
