package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserDeviceToken;
import com.BioTy.Planty.repository.UserDeviceTokenRepository;
import com.BioTy.Planty.repository.UserRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final UserDeviceTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;

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
    public void sendMessage(String targetToken, String title, String body){
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();
        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM 메시지 전송 성공: {}", response);
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패: {}", e);
        }
    }

}
