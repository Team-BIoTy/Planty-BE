package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.userPlant.UserPlantCreateRequestDto;
import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "UserPlant", description = "사용자의 반려식물 관련 API")
@RestController
@RequestMapping("/user-plants")
@RequiredArgsConstructor
public class UserPlantController {
    private final UserPlantService userPlantService;
    private final AuthService authService;

    // 로그인한 사용자의 반려 식물 조회 (홈)
    @Operation(summary = "사용자의 반려식물 목록 조회",
            description = "JWT 토큰을 통해 사용자의 식물 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<List<UserPlantSummaryResponseDto>> getUserPlants(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        List<UserPlantSummaryResponseDto> response = userPlantService.getUserPlants(userId);
        return ResponseEntity.ok(response);
    }

    // 사용자 식물 등록
    @PostMapping
    @Operation(
            summary = "반려식물 등록",
            description = "사용자가 선택한 식물 정보(도감), 애칭, 입양일, 성격을 입력하여 반려식물을 등록합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Long> registerUserPlant(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @RequestBody UserPlantCreateRequestDto requestDto
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        Long userPlantId = userPlantService.registerUserPlant(requestDto, userId);
        return ResponseEntity.ok(userPlantId);
    }
}
