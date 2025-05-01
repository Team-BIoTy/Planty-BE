package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.chat.ChatIdResponseDto;
import com.BioTy.Planty.dto.chat.ChatMessageResponseDto;
import com.BioTy.Planty.entity.ChatMessage;
import com.BioTy.Planty.entity.ChatRoom;
import com.BioTy.Planty.repository.ChatMessageRepository;
import com.BioTy.Planty.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 1. 채팅 시작
    public ChatIdResponseDto startChat(Long userId, Long userPlantId){
        ChatRoom chatRoom = chatRoomRepository.findByUserIdAndUserPlantId(userId, userPlantId)
                .orElseGet(() -> chatRoomRepository.save(
                        new ChatRoom(userId, userPlantId, LocalDateTime.now())
                ));
        return new ChatIdResponseDto(chatRoom.getId());
    }

    // 2. 메시지 리스트 조회
    public List<ChatMessageResponseDto> getMessages(Long chatRoomId){
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId).stream()
                .map(msg -> new ChatMessageResponseDto(
                        msg.getSender().name(),
                        msg.getMessage(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());
    }


    // 3. 메시지 전송
    public ChatMessageResponseDto sendMessage(Long chatRoomId, String message) {
        // 사용자 메시지 저장
        ChatMessage userMsg = chatMessageRepository.save(new ChatMessage(chatRoomId, ChatMessage.Sender.USER, message));

        // AI 서버와 통신해서 응답 받아오기 (TODO)
        String botReply = "temp"; // 임시

        // 챗봇 메시지 저장
        ChatMessage botMsg = chatMessageRepository.save(new ChatMessage(chatRoomId, ChatMessage.Sender.BOT, botReply));

        return new ChatMessageResponseDto(botMsg.getSender().name(), botMsg.getMessage(), botMsg.getTimestamp());
    }
}
