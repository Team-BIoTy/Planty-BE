package com.BioTy.Planty.repository;

import com.BioTy.Planty.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByUserIdAndUserPlantId(Long userId, Long userPlantId);
    List<ChatRoom> findAllByUserIdOrderByLastSentAtDesc(Long userId);
}
