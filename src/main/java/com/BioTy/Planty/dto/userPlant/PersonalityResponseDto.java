package com.BioTy.Planty.dto.userPlant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonalityResponseDto {
    private Long id;
    private String label;
    private String emoji;
    private String description;
    private String exampleComment;
}
