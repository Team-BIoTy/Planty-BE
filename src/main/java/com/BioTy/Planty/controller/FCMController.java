package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.fcm.DeviceTokenRequestDto;
import com.BioTy.Planty.dto.fcm.SendNotificationRequestDto;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.FCMService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fcm")
@Tag(name = "FCMController", description = "Firebase Cloud Messaging 관련 API")
@RequiredArgsConstructor
public class FCMController {
    private final FCMService fcmService;
    private final AuthService authService;

    @PostMapping("/token")
    @Operation(
            summary = "디바이스 토큰 저장",
            description = "Firebase 알림 전송을 위해 사용자의 FCM 디바이스 토큰을 저장합니다."
    )
    public ResponseEntity<Void> saveDeviceToken(
            @RequestBody DeviceTokenRequestDto request,
            @Parameter(hidden = true) @RequestHeader("Authorization") String token
    ) {
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        fcmService.saveOrUpdateToken(userId, request.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/send")
    @Operation(
            summary = "푸시 알림 테스트 전송",
            description = "디바이스 토큰을 이용해 알림을 직접 전송합니다."
    )
    public ResponseEntity<Void> sendNotification(
            @RequestBody SendNotificationRequestDto request
            ){
        fcmService.sendMessage(request.getTargetToken(), request.getTitle(), request.getBody());
        return ResponseEntity.ok().build();
    }
}
