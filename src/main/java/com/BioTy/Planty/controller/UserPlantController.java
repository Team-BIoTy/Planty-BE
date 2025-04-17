package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
