package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.chat.ChatIdResponseDto;
import com.BioTy.Planty.dto.chat.ChatMessageResponseDto;
import com.BioTy.Planty.dto.chat.ChatRoomDetailDto;
import com.BioTy.Planty.dto.chat.ChatRoomSummaryDto;
import com.BioTy.Planty.entity.*;
import com.BioTy.Planty.repository.ChatMessageRepository;
import com.BioTy.Planty.repository.ChatRoomRepository;
import com.BioTy.Planty.repository.PlantEnvStandardsRepository;
import com.BioTy.Planty.repository.SensorLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SensorLogsRepository sensorLogsRepository;
    private final PlantEnvStandardsRepository plantEnvStandardsRepository;

    private final RestTemplate restTemplate = new RestTemplate();

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

    // 3. 채팅방 상세 조회 (반려식물 정보 + 메시지)
    public ChatRoomDetailDto getChatRoomDetail(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));

        UserPlant userPlant = chatRoom.getUserPlant(); // null일 수 있음

        Long userPlantId = (userPlant != null) ? userPlant.getId() : -1L;
        String nickname = userPlant != null ? userPlant.getNickname() : "식물 챗봇";
        String imageUrl = userPlant != null ? userPlant.getImageUrl() : null;
        String personalityLabel = userPlant != null ? userPlant.getPersonality().getLabel() : null;
        String personalityEmoji = userPlant != null ? userPlant.getPersonality().getEmoji() : null;
        String personalityColor = userPlant != null ? userPlant.getPersonality().getColor() : null;

        // Iot 디바이스가 null일 수 있음
        Long sensorLogId = null;
        if (userPlant != null && userPlant.getIotDevice() != null) {
            SensorLogs latestLog = sensorLogsRepository
                    .findTopByIotDevice_IdOrderByRecordedAtDesc(userPlant.getIotDevice().getId())
                    .orElse(null);
            if (latestLog != null) {
                sensorLogId = latestLog.getId();
            }
        }
        // 환경기준값이 null일 수 있음
        Long plantEnvStandardsId = null;
        if (userPlant != null && userPlant.getPlantInfo() != null) {
            plantEnvStandardsId = plantEnvStandardsRepository
                    .findByPlantInfo(userPlant.getPlantInfo())
                    .map(PlantEnvStandards::getId)
                    .orElse(null);
        }

        List<ChatMessageResponseDto> messages = chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId).stream()
                .map(msg -> new ChatMessageResponseDto(
                        msg.getSender().name(),
                        msg.getMessage(),
                        msg.getTimestamp()
                ))
                .collect(Collectors.toList());

        PlantInfo plantInfo = null;
        if (userPlant != null) {
            plantInfo = userPlant.getPlantInfo();
        }

        return new ChatRoomDetailDto(
                chatRoomId,
                userPlantId,
                nickname,
                imageUrl,
                personalityLabel,
                personalityEmoji,
                personalityColor,
                sensorLogId,
                plantEnvStandardsId,
                messages,
                plantInfo
        );
    }


    // 4. 메시지 전송
    public ChatMessageResponseDto sendMessage(Long chatRoomId, String message, PlantInfo info,
                                              Long sensorLogId, Long plantEnvStandardsId, String persona) {
        // 1) 사용자 메시지 저장
        ChatMessage userMsg = chatMessageRepository.save(
                new ChatMessage(chatRoomId, ChatMessage.Sender.USER, message)
        );

        // 2) Agent 서버 호출
        String botReply = "기본 응답입니다 (Agent 연결 실패)";
        try {
            String aiServerUrl = "http://localhost:8000/chat";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("chat_room_id", chatRoomId);
            requestBody.put("sensor_log_id", sensorLogId);
            requestBody.put("plant_env_standards_id", plantEnvStandardsId);
            requestBody.put("persona", persona);
            requestBody.put("user_input", message);

            Map<String, Object> plantInfo = new HashMap<>();
            PlantInfo plant = info;

            if (plant != null) {
                plantInfo.put("id", plant.getId());
                plantInfo.put("imageUrl", plant.getImageUrl());
                plantInfo.put("commonName", plant.getCommonName());
                plantInfo.put("scientificName", plant.getScientificName());
                plantInfo.put("englishName", plant.getEnglishName());
                plantInfo.put("tradeName", plant.getTradeName());
                plantInfo.put("familyName", plant.getFamilyName());
                plantInfo.put("origin", plant.getOrigin());
                plantInfo.put("careTip", plant.getCareTip());

                plantInfo.put("category", plant.getCategory());
                plantInfo.put("growthForm", plant.getGrowthForm());
                plantInfo.put("growthHeight", plant.getGrowthHeight());
                plantInfo.put("growthWidth", plant.getGrowthWidth());
                plantInfo.put("indoorGardenUse", plant.getIndoorGardenUse());
                plantInfo.put("ecologicalType", plant.getEcologicalType());
                plantInfo.put("leafShape", plant.getLeafShape());
                plantInfo.put("leafPattern", plant.getLeafPattern());
                plantInfo.put("leafColor", plant.getLeafColor());

                plantInfo.put("floweringSeason", plant.getFloweringSeason());
                plantInfo.put("flowerColor", plant.getFlowerColor());
                plantInfo.put("fruitingSeason", plant.getFruitingSeason());
                plantInfo.put("fruitColor", plant.getFruitColor());
                plantInfo.put("fragrance", plant.getFragrance());
                plantInfo.put("propagationMethod", plant.getPropagationMethod());
                plantInfo.put("propagationSeason", plant.getPropagationSeason());

                plantInfo.put("careLevel", plant.getCareLevel());
                plantInfo.put("careDifficulty", plant.getCareDifficulty());
                plantInfo.put("lightRequirement", plant.getLightRequirement());
                plantInfo.put("placement", plant.getPlacement());
                plantInfo.put("growthRate", plant.getGrowthRate());
                plantInfo.put("optimalTemperature", plant.getOptimalTemperature());
                plantInfo.put("minWinterTemperature", plant.getMinWinterTemperature());
                plantInfo.put("humidity", plant.getHumidity());
                plantInfo.put("fertilizer", plant.getFertilizer());
                plantInfo.put("soilType", plant.getSoilType());

                plantInfo.put("wateringSpring", plant.getWateringSpring());
                plantInfo.put("wateringSummer", plant.getWateringSummer());
                plantInfo.put("wateringAutumn", plant.getWateringAutumn());
                plantInfo.put("wateringWinter", plant.getWateringWinter());
                plantInfo.put("pestsDiseases", plant.getPestsDiseases());

                plantInfo.put("functionalInfo", plant.getFunctionalInfo());
            }

            requestBody.put("plant_info", plantInfo);


            System.out.println("🔍 [Agent 요청] " + requestBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiServerUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                botReply = (String) response.getBody().get("final_response");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3) 챗봇 메시지 저장
        ChatMessage botMsg = chatMessageRepository.save(
                new ChatMessage(chatRoomId, ChatMessage.Sender.BOT, botReply)
        );

        // 4) 채팅방의 lastSenetAt 갱신
        chatRoomRepository.findById(chatRoomId).ifPresent(room -> {
            room.updateLastSentAt(LocalDateTime.now());
            chatRoomRepository.save(room);
        });

        return new ChatMessageResponseDto(
                botMsg.getSender().name(),
                botMsg.getMessage(),
                botMsg.getTimestamp()
        );
    }

    public String callPlantQAAgent(Long chatRoomId, String userInput) {
        String aiServerUrl = "http://localhost:8000/plant_qa";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_input", userInput);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        String botReply = "Agent 응답 실패";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiServerUrl, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                botReply = (String) response.getBody().get("final_response");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chatRoomId != null) {
            chatMessageRepository.save(new ChatMessage(chatRoomId, ChatMessage.Sender.USER, userInput));
            chatMessageRepository.save(new ChatMessage(chatRoomId, ChatMessage.Sender.BOT, botReply));
            chatRoomRepository.findById(chatRoomId).ifPresent(room -> {
                room.updateLastSentAt(LocalDateTime.now());
                chatRoomRepository.save(room);
            });
        }

        return botReply;
    }


}
