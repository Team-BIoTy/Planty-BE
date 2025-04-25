package com.BioTy.Planty.dto.userPlant.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DetailSensorDto {
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal light;
    private LocalDateTime recordedAt;
}
