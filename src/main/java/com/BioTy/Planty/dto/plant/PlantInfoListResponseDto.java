package com.BioTy.Planty.dto.plant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlantInfoListResponseDto {
    private Long plantInfoId;
    private String commonName;
    private String englishName;
    private String imageUrl;
}
