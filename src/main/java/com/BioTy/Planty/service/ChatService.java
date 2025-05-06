package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.chat.ChatIdResponseDto;
import com.BioTy.Planty.dto.chat.ChatMessageResponseDto;
import com.BioTy.Planty.dto.chat.ChatRoomDetailDto;
import com.BioTy.Planty.dto.chat.ChatRoomSummaryDto;
import com.BioTy.Planty.entity.ChatMessage;
import com.BioTy.Planty.entity.ChatRoom;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.ChatMessageRepository;
import com.BioTy.Planty.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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

    // 2. 채팅방 목록 조회
    @Transactional(readOnly = false)
    public List<ChatRoomSummaryDto> getChatRooms(Long userId){
        List<ChatRoom> rooms =
                chatRoomRepository.findAllByUserIdOrderByLastSentAtDesc(userId);

        return rooms.stream().map(room -> {
            ChatMessage lastMsg = chatMessageRepository
                    .findTopByChatRoomIdOrderByTimestampDesc(room.getId())
                    .orElse(null);

            String nickname = room.getUserPlant() != null
                    ? room.getUserPlant().getNickname()
                    : "식물챗봇";

            String imageUrl = room.getUserPlant() != null
                    ? room.getUserPlant().getImageUrl()
                    : "";

            return new ChatRoomSummaryDto(
                    room.getId(),
                    nickname,
                    lastMsg != null ? lastMsg.getMessage() : "(아직 메시지 없음)",
                    lastMsg != null ? lastMsg.getTimestamp() : room.getLastSentAt(),
                    imageUrl
            );
        }).collect(Collectors.toList());
    }

    // 3. 메시지 리스트 조회
    public List<ChatMessageResponseDto> getMessages(Long chatRoomId){
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId).stream()
                .map(msg -> new ChatMessageResponseDto(
                        msg.getSender().name(),
                        msg.getMessage(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    // 4. 채팅방 상세 조회 (반려식물 정보 + 메시지)
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        UserPlant userPlant = chatRoom.getUserPlant(); // null일 수 있음

        String nickname = userPlant != null ? userPlant.getNickname() : "식물 챗봇";
        String imageUrl = userPlant != null ? userPlant.getImageUrl() : null;
        String personalityLabel = userPlant != null ? userPlant.getPersonality().getLabel() : null;
        String personalityEmoji = userPlant != null ? userPlant.getPersonality().getEmoji() : null;
        String personalityColor = userPlant != null ? userPlant.getPersonality().getColor() : null;

        List<ChatMessageResponseDto> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId).stream()
                .map(msg -> new ChatMessageResponseDto(
                        msg.getSender().name(),
                        msg.getMessage(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());

        return new ChatRoomDetailDto(
                chatRoomId,
                nickname,
                imageUrl,
                personalityLabel,
                personalityEmoji,
                personalityColor,
                messages
        );
    }


    // 5. 메시지 전송
    public ChatMessageResponseDto sendMessage(Long chatRoomId, String message) {
        // 1) 사용자 메시지 저장
        ChatMessage userMsg = chatMessageRepository.save(
                new ChatMessage(chatRoomId, ChatMessage.Sender.USER, message)
        );

        // 2) AI 서버와 통신해서 챗봇 응답 받기 & 저장 (TODO)
        String botReply = "테스트 응답입니다"; // 임시
        ChatMessage botMsg = chatMessageRepository.save(
                new ChatMessage(chatRoomId, ChatMessage.Sender.BOT, botReply)
        );

        // 3) 채팅방의 lastSenetAt 갱신
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));
        chatRoom.updateLastSentAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        return new ChatMessageResponseDto(
                botMsg.getSender().name(),
                botMsg.getMessage(),
                botMsg.getTimestamp()
        );
    }
}
