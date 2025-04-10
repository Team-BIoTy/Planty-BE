package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.user.SignupRequestDto;
import com.BioTy.Planty.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 1. 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        authService.signup(signupRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }
}
