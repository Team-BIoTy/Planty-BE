package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.userPlant.PersonalityResponseDto;
import com.BioTy.Planty.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Personality", description = "식물 성격 관련 API")
@RestController
@RequestMapping("/personalities")
@RequiredArgsConstructor
public class PersonalityController {
    private final UserPlantService userPlantService;

    // 성격 조회
    @GetMapping
    @Operation(
            summary = "식물 성격 목록 조회",
            description = "반려식물 등록 시 선택할 수 있는 성격 목록을 반환합니다."
    )
    public List<PersonalityResponseDto> getAllPersonalities(){
        return userPlantService.getAllPersonalities();
    }
}
