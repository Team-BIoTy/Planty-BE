package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.iot.ActionRequestDto;
import com.BioTy.Planty.dto.iot.IotDeviceResponseDto;
import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.DeviceCommandService;
import com.BioTy.Planty.service.IotService;
import com.BioTy.Planty.service.UserPlantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Iot", description = "사용자의 IoT 기기 관련 API")
@RestController
@RequestMapping("/iot")
@RequiredArgsConstructor
public class IotController {
    private final IotService iotService;
    private final AuthService authService;
    private final UserPlantService userPlantService;
    private final DeviceCommandService deviceCommandService;

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

        return ResponseEntity.ok(iotService.getDevicesByUserId(userId));
    }

    @Operation(
            summary = "IoT 센서 데이터 수동 수신",
            description = "선택한 IoT 기기의 센서 데이터를 Adafruit에서 수신하여 sensor_logs에 저장합니다."
    )
    @PostMapping("/sensor-data")
    public ResponseEntity<Void> fetchSensorData(
        @RequestParam Long deviceId
    ){
        iotService.fetchAndSaveSensorLog(deviceId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "IoT 액션 명령 전송",
            description = "선택한 반려식물의 IoT 장치에 명령(WATER, FAN, LIGHT)을 전송합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/{userPlantId}/actions")
    public ResponseEntity<Void> sendActionToAdafruit(
            @Parameter(description = "반려식물 ID") @PathVariable("userPlantId") Long userPlantId,
            @RequestBody ActionRequestDto request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);
        UserPlant userPlant = userPlantService.getUserPlantEntity(userPlantId, userId);
        deviceCommandService.executeCommands(userPlant, List.of(request.getType()));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/commands/{commandId}/cancel")
    @Operation(summary = "명령 취소", description = "사용자가 진행 중인 명령을 중단합니다.")
    public ResponseEntity<Void> cancelCommand(
            @PathVariable("commandId") Long commandId,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token.replace("Bearer ", ""));
        deviceCommandService.cancelCommand(commandId, userId);
        return ResponseEntity.ok().build();
    }

}
