package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.iot.IotDeviceResponseDto;
import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.IotService;
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
import java.util.stream.Collectors;

@Tag(name = "Iot", description = "사용자의 IoT 기기 관련 API")
@RestController
@RequestMapping("/iot")
@RequiredArgsConstructor
public class IotController {
    private final IotService iotService;
    private final AuthService authService;

    @Operation(
            summary = "사용자의 IoT 기기 목록 조회",
            description = "JWT 토큰을 통해 현재 사용자의 IoT 기기 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<List<IotDeviceResponseDto>> getMyDevices(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);
        List<IotDevice> devices = iotService.getDevicesByUserId(userId);

        List<IotDeviceResponseDto> response = devices.stream()
                .map(IotDeviceResponseDto::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
