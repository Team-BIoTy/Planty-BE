package com.BioTy.Planty.controller;

import com.BioTy.Planty.dto.chat.*;
import com.BioTy.Planty.service.AuthService;
import com.BioTy.Planty.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "식물 챗봇 관련 API")
public class ChatController {
    private final ChatService chatService;
    private final AuthService authService;

    @PostMapping
    @Operation(
            summary = "채팅방 생성",
            description = "userPlantId가 null이면 일반 식물 챗봇, 아니면 반려식물 챗봇으로 연결",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChatIdResponseDto> startCht(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token,
            @RequestBody StartChatRequestDto request
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        ChatIdResponseDto chatRoomId = chatService.startChat(userId, request.getUserPlantId());
        return ResponseEntity.ok(chatRoomId);
    }

    @GetMapping
    @Operation(
            summary = "채팅방 목록 조회",
            description = "사용자의 전체 채팅방 목록을 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<ChatRoomSummaryDto>> getChatRooms(
            @Parameter(hidden = true)
            @RequestHeader("Authorization") String token
    ){
        token = token.replace("Bearer ", "");
        Long userId = authService.getUserIdFromToken(token);

        List<ChatRoomSummaryDto> chatRooms = chatService.getChatRooms(userId);
        return ResponseEntity.ok(chatRooms);
    }


    @GetMapping("/{chatRoomId}")
    @Operation(
            summary = "채팅방 상세 조회",
            description = "채팅방 정보 및 메시지 목록을 함께 조회",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChatRoomDetailDto> getChatRoomDetail(@PathVariable("chatRoomId") Long chatRoomId) {
        ChatRoomDetailDto response = chatService.getChatRoomDetail(chatRoomId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/{chatRoomId}/messages")
    @Operation(
            summary = "채팅 메시지 전송",
            description = "사용자 메시지를 전송하면 챗봇 응답까지 함께 반환",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ChatMessageResponseDto> sendMessage(
            @PathVariable("chatRoomId") Long chatRoomId,
            @RequestBody SendMessageRequestDto request
            ){
        ChatMessageResponseDto response = chatService.sendMessage(
                chatRoomId,
                request.getMessage(),
                request.getPlantInfo(),
                request.getSensorLogId(),
                request.getPlantEnvStandardsId(),
                request.getPersona()
        );
        return ResponseEntity.ok(response);
    }
}
