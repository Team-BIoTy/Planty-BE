package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private IotDevice iotDevice;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserPlant userPlant;

    private String commandType; // WATER, LIGHT, FAN
    private LocalDateTime sentAt;
}
