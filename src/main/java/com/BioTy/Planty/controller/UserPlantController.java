package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.environment.EnvironmentReportDto;
import com.BioTy.Planty.dto.iot.RegisterDeviceRequestDto;
import com.BioTy.Planty.dto.userPlant.UserPlantCreateRequestDto;
import com.BioTy.Planty.dto.userPlant.UserPlantDetailResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.dto.userPlant.UserPlantEditDto;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.EnvironmentReportService;
import com.BioTy.Planty.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "UserPlant", description = "사용자의 반려식물 관련 API")
@RestController
@RequestMapping("/user-plants")
@RequiredArgsConstructor
public class UserPlantController {
    private final UserPlantService userPlantService;
    private final AuthService authService;
    private final EnvironmentReportService environmentReportService;

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
    @Operation(
            summary = "반려식물 등록",
            description = "사용자가 선택한 식물 정보(도감), 애칭, 입양일, 성격을 입력하여 반려식물을 등록합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
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

    // iot 기기 등록
    @Operation(
            summary = "IoT 기기 연결",
            description = "사용자의 반려식물과 IoT 기기를 연결합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{userPlantId}/device")
    public ResponseEntity<Void> registerIotDevice(
            @Parameter(description = "반려식물 ID") @PathVariable("userPlantId") Long userPlantId,
            @RequestBody RegisterDeviceRequestDto requestDto
    ){
        userPlantService.registerDevice(userPlantId, requestDto.getIotDeviceId());
        return ResponseEntity.ok().build();
    }

    // 반려식물 상세 정보 조회
    @Operation(
            summary = "반려식물 상세 조회",
            description = "userPlantId를 이용해 반려식물의 상세 정보를 조회합니다.")
    @GetMapping("/{userPlantId}")
    public UserPlantDetailResponseDto getUserPlantDetail(@PathVariable("userPlantId") Long userPlantId) {
        return userPlantService.getUserPlantDetail(userPlantId);
    }

    // 반려식물 삭제
    @Operation(
            summary = "반려식물 삭제",
            description = "userPlantId를 이용해 반려식물을 삭제합니다. (연결된 IoT 디바이스도 자동으로 해제)")
    @DeleteMapping("/{userPlantId}")
    public ResponseEntity<Void> deleteUserPlant(
            @PathVariable("userPlantId") Long userPlantId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        userPlantService.deleteUserPlant(userPlantId, userId);
        return ResponseEntity.noContent().build();
    }

    // 반려식물 수정용 조회
    @Operation(
            summary = "수정용 반려식물 정보 조회",
            description = "수정을 위한 반려식물의 정보를 조회합니다.")
    @GetMapping("/edit/{userPlantId}")
    public UserPlantEditDto getUserPlantForEdit(
            @PathVariable("userPlantId") Long userPlantId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        return userPlantService.getUserPlantForEdit(userPlantId, userId);
    }

    // 반려식물 수정
    @Operation(
            summary = "반려식물 정보 수정",
            description = "반려식물의 정보를 수정합니다.")
    @PutMapping("/edit/{userPlantId}")
    public ResponseEntity<Void> editUserPlant(
            @PathVariable("userPlantId") Long userPlantId,
            @RequestBody UserPlantEditDto requestDto,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        userPlantService.editUserPlant(userPlantId, userId, requestDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "환경 리포트 조회", description = "특정 반려식물의 하루치 환경 리포트를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{userPlantId}/environment-report")
    public ResponseEntity<EnvironmentReportDto> getEnvironmentReport(
            @PathVariable("userPlantId") Long userPlantId,
            @RequestParam(name = "date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        EnvironmentReportDto dto = environmentReportService.getReport(userPlantId, date);
        return ResponseEntity.ok(dto);
    }


}
