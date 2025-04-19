package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserPlantCreateRequestDto {
    private Long plantInfoId;
    private String nickname;
    private LocalDate adoptedAt;
    private Long personalityId;
    private String imageUrl;
}
