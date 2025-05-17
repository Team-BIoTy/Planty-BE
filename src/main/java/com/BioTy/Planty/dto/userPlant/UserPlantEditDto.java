package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPlantEditDto {
    private String nickname;
    private LocalDate adoptedAt;
    private Boolean autoControlEnabled;
    private Long personalityId;
    private Long deviceId;
}
