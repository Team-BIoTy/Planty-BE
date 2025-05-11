package com.BioTy.Planty.dto.iot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class SensorLogResponseDto {
    private String key;

    @JsonProperty("last_value")
    private String lastValue;

    @JsonProperty("updated_at")
    private String updatedAt;
}
