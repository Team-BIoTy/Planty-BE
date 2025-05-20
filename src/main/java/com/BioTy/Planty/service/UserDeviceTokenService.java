package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserDeviceToken;
import com.BioTy.Planty.repository.UserDeviceTokenRepository;
import com.BioTy.Planty.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDeviceTokenService {
    private final UserDeviceTokenRepository tokenRepository;
    private final UserRepository userRepository;

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

}
