package com.BioTy.Planty.dto.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentReportDto {
    private String plantName;
    private boolean isAutoMode;
    private List<SensorDataPoint> temperature;
    private List<SensorDataPoint> light;
    private List<SensorDataPoint> humidity;
    private EnvironmentRangeDto recommended;
}
