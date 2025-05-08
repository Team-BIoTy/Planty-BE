package com.BioTy.Planty.dto.iot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActionRequestDto {
    private String type; // "WATER", "FAN", "LIGHT"
}
