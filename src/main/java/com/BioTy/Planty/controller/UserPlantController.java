package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.UserPlantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user-plants")
@RequiredArgsConstructor
public class UserPlantController {
    private final UserPlantService userPlantService;
    private final AuthService authService;

    // 로그인한 사용자의 반려 식물 조회 (홈)
    @GetMapping
    public ResponseEntity<List<UserPlantSummaryResponseDto>> getUserPlants(
            @RequestHeader("Authorization") String token
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        List<UserPlantSummaryResponseDto> response = userPlantService.getUserPlants(userId);
        return ResponseEntity.ok(response);
    }
}
