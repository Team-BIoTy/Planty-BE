package com.BioTy.Planty.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private IotDevice iotDevice;

    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal light;

    private LocalDateTime recordedAt;

    public SensorLogs(IotDevice iotDevice, BigDecimal temperature, BigDecimal humidity, BigDecimal light, LocalDateTime recordedAt) {
        this.iotDevice = iotDevice;
        this.temperature = temperature;
        this.humidity = humidity;
        this.light = light;
        this.recordedAt = recordedAt;
    }
}
