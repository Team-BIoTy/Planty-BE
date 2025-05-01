package com.BioTy.Planty.dto.userPlant.detail;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DetailStatusDto {
    private Integer temperatureScore; // 온도 점수 (0~3)
    private Integer lightScore; // 조도 점수 (0~3)
    private Integer humidityScore; // 습도 점수 (0~3)
    private String statusMessage;
    private LocalDateTime checkedAt;
}
