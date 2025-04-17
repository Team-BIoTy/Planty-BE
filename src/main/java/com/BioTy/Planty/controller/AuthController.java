package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.user.LoginRequestDto;
import com.BioTy.Planty.dto.user.LoginResponseDto;
import com.BioTy.Planty.dto.user.SignupRequestDto;
import com.BioTy.Planty.security.JwtUtil;
import com.BioTy.Planty.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "회원가입, 로그인, 토큰 검증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 1. 회원가입
    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입합니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequestDto){
        authService.signup(signupRequestDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 2. 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    // 3. 토큰 유효성 검사
    @Operation(summary = "토큰 유효성 검사", description = "JWT 토큰이 유효한지 검사합니다.")
    @GetMapping("/validate")
    public ResponseEntity<Void> validateToken(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        boolean isValid = jwtUtil.validateToken(token);

        if(!isValid){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().build();
    }
}
