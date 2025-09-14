package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.user.LoginRequestDto;
import com.BioTy.Planty.dto.user.LoginResponseDto;
import com.BioTy.Planty.dto.user.SignupRequestDto;
import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.repository.IotRepository;
import com.BioTy.Planty.repository.UserRepository;
import com.BioTy.Planty.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final IotRepository iotRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 1. 회원가입 메서드
    public void signup(SignupRequestDto signupRequestDto) {
        if(userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = new User(signupRequestDto.getEmail(), encodedPassword);

        userRepository.save(user);
    }

    // 2. 로그인 메서드
    public LoginResponseDto login(LoginRequestDto loginRequestDto){
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponseDto(token);
    }

    // 3. 토큰 → userId 추출
    public Long getUserIdFromToken(String token) {
        String email = jwtUtil.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."))
                .getId();
    }

    // 4. 비밀번호 변경
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 5. 회원탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
    }

    // 6. 유저 정보 불러오기
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Transactional
    public void updateAdafruitAccount(Long userId, String username, String apiKey) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updateAdafruitAccount(username, apiKey);
        userRepository.save(user);

        if (user.getIotDevices().isEmpty()) {
            IotDevice device = new IotDevice();
            device.setUser(user);
            device.setDeviceSerial(UUID.randomUUID().toString()); // 임시 시리얼
            device.setModel("Adafruit-Default");
            device.setStatus("ACTIVE");
            device.setFeedKey("planty");
            iotRepository.save(device);
        }
    }

}
