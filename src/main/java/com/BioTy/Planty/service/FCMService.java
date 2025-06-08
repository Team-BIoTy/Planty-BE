package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserDeviceToken;
import com.BioTy.Planty.entity.UserNotification;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.UserDeviceTokenRepository;
import com.BioTy.Planty.repository.UserNotificationRepository;
import com.BioTy.Planty.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final UserDeviceTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final UserNotificationRepository userNotificationRepository;

    // 유저 ID 기준으로 FCM 토큰 저장 또는 수정
    @Transactional
    public void saveOrUpdateToken(Long userId, String token){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Optional<UserDeviceToken> existing = tokenRepository.findByUser(user);

        if(existing.isPresent()){ // 이미 토큰이 존재하는 경우 -> 업데이트
            UserDeviceToken deviceToken = existing.get();
            deviceToken.setToken(token);
        } else{ // 처음 저장하는 경우
            UserDeviceToken newToken = new UserDeviceToken();
            newToken.setUser(user);
            newToken.setToken(token);
            tokenRepository.save(newToken);
        }
    }

    // 전달받은 디바이스 토큰에 푸시 메시지 전송
    public void sendMessage(User user, UserPlant userPlant, String targetToken, String title, String body){
        Message message = Message.builder()
                .setToken(targetToken)
                .putData("title", title)
                .putData("body", body)
                .build();
        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM 메시지 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패: {}", e);
        }

        userNotificationRepository.save(UserNotification.builder()
                .user(user)
                .userPlant(userPlant)
                .title(title)
                .body(body)
                .receivedAt(LocalDateTime.now())
                .build());
    }

}
