package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);
    Optional<ChatMessage> findTopByChatRoomIdOrderByTimestampDesc(Long chatRoomId);
}
