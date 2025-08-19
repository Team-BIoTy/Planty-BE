package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserPlantCreateRequestDto {
    private Long plantInfoId;
    private String nickname;
    private LocalDate adoptedAt;
    private Long personalityId;
    private String imageUrl;
}
