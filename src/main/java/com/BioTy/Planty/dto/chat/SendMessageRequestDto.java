package com.BioTy.Planty.dto.chat;

import com.BioTy.Planty.entity.PlantInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    private String type; // slm 또는 llm
    private String message;
    private PlantInfo plantInfo;
    private Long sensorLogId;
    private Long plantEnvStandardsId;
    private String persona;
}
