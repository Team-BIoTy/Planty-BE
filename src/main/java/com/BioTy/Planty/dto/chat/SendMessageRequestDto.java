package com.BioTy.Planty.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDto {
    private String message;
    private Long sensorLogId;
    private Long plantEnvStandardsId;
    private String persona;
}
