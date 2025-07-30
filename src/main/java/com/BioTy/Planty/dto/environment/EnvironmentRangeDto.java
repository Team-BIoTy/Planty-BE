package com.BioTy.Planty.dto.environment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EnvironmentRangeDto {
    private Range temperature;
    private Range light;
    private Range humidity;
}
