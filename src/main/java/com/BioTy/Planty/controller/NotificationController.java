package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.notification.NotificationResponseDto;
import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserNotification;
import com.BioTy.Planty.repository.UserNotificationRepository;
import com.BioTy.Planty.repository.UserRepository;
import com.BioTy.Planty.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "NotificationController", description = "알림 관련 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final AuthService authService;

    @Operation(summary = "사용자 알림 목록 조회", description = "특정 사용자의 알림 목록을 최신순으로 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/me")
    public List<NotificationResponseDto> getMyNotifications(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token) {
        Long userId = authService.getUserIdFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        return userNotificationRepository.findByUserOrderByReceivedAtDesc(user).stream()
                .map(NotificationResponseDto::from)
                .toList();
    }

    @Operation(summary = "전체 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/me/read-all")
    public void markAllAsRead(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        Long userId = authService.getUserIdFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        List<UserNotification> unreadNotifications = userNotificationRepository.findByUserAndIsReadFalse(user);
        for (UserNotification notification : unreadNotifications) {
            notification.setRead(true);
        }
        userNotificationRepository.saveAll(unreadNotifications);
    }


}
