package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.user.*;
import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.security.JwtUtil;
import com.BioTy.Planty.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

    // 4. 비밀번호 변경
    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @RequestBody ChangePasswordRequestDto request
    ) {
        try {
            token = token.replace("Bearer ", "");
            Long userId = authService.getUserIdFromToken(token);

            authService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // 5. 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴시킵니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/delete")
    public ResponseEntity<?> withdraw(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ) {
        try {
            token = token.replace("Bearer ", "");
            Long userId = authService.getUserIdFromToken(token);
            authService.deleteUser(userId);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 6. 내 정보 조회
    @Operation(summary = "내 정보 조회", description = "JWT 토큰을 이용해 이메일과 가입일을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 유효하지 않습니다.");
        }

        String token = authHeader.substring(7);
        Long userId = authService.getUserIdFromToken(token);

        User user = authService.getUserInfo(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("email", user.getEmail());
        result.put("joinedDate", user.getCreatedAt());

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Adafruit 계정 등록 및 수정", description = "Adafruit username과 apiKey를 등록/수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/adafruit")
    public ResponseEntity<?> updateAdafruitAccount(
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateAdafruitAccountDto request
    ) {
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);
        authService.updateAdafruitAccount(userId, request.getUsername(), request.getApiKey());
        return ResponseEntity.ok("Adafruit 계정이 업데이트되었습니다.");
    }

}
