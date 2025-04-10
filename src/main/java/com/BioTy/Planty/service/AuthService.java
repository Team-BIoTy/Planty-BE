package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.user.SignupRequestDto;
import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 1. 회원가입 메서드
    public void signup(SignupRequestDto signupRequestDto) {
        if(userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequestDto.getPassword());
        User user = new User(signupRequestDto.getEmail(), encodedPassword);

        userRepository.save(user);
    }
}
