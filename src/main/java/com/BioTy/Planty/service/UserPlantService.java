package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.userPlant.*;
import com.BioTy.Planty.entity.*;
import com.BioTy.Planty.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPlantService {
    private final UserPlantRepository userPlantRepository;
    private final UserRepository userRepository;
    private final PlantInfoRepository plantInfoRepository;
    private final PersonalityRepository personalityRepository;
    private final IotRepository iotRepository;
    private final IotService iotService;
    private final PlantStatusService plantStatusService;

    // 사용자 반려식물 목록 조회
    public List<UserPlantSummaryResponseDto> getUserPlants(Long userId){
        return userPlantRepository.findSummaryDtoByUserId(userId);
    }

    // 반려식물 등록
    @Transactional
    public Long registerUserPlant(UserPlantCreateRequestDto requestDto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        PlantInfo plantInfo = plantInfoRepository.findById(requestDto.getPlantInfoId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 식물입니다."));
        Personality personality = personalityRepository.findById(requestDto.getPersonalityId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 성격입니다."));

        UserPlant userPlant = UserPlant.builder()
                .user(user)
                .nickname(requestDto.getNickname())
                .imageUrl(requestDto.getImageUrl())
                .adoptedAt(requestDto.getAdoptedAt())
                .personality(personality)
                .plantInfo(plantInfo)
                .build();

        userPlantRepository.save(userPlant);
        return userPlant.getId();
    }

    // 성격 조회
    public List<PersonalityResponseDto> getAllPersonalities(){
        return personalityRepository.findAll().stream()
                .map(p -> new PersonalityResponseDto(
                        p.getId(),
                        p.getLabel(),
                        p.getEmoji(),
                        p.getColor(),
                        p.getDescription(),
                        p.getExampleComment()
                ))
                .toList();
    }

    // 반려식물에 IoT 기기 등록
    @Transactional
    public void registerDevice(Long userPlantId, Long iotDeviceId){
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new IllegalArgumentException("해당 반려식물이 존재하지 않습니다."));
        IotDevice iotDevice = iotRepository.findById(iotDeviceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 IoT 기기가 존재하지 않습니다."));

        userPlant.setIotDevice(iotDevice);
        iotDevice.setUserPlant(userPlant);

        try {
            iotService.fetchAndSaveSensorLog(iotDeviceId);
            plantStatusService.evaluatePlantStatus(iotDeviceId);
        } catch (Exception e) {
            System.err.println("센서 수집 또는 상태 평가 실패: " + e.getMessage());
        }
    }

    // 반려식물 상세 정보 조회
    @Transactional
    public UserPlantDetailResponseDto getUserPlantDetail(Long userPlantId) {
        return userPlantRepository.findDetailDtoByUserPlantId(userPlantId);
    }

    // 반려식물 삭제
    @Transactional
    public void deleteUserPlant(Long userPlantId, Long userId){
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new IllegalArgumentException("반려식물을 찾을 수 없습니다."));
        // 본인 소유의 반려식물이 아닌 경우
        if(!userPlant.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 반려식물만 삭제할 수 있습니다.");
        }

        // IoT 디바이스 연결 해제
        IotDevice iotDevice = userPlant.getIotDevice();
        if (iotDevice != null){
            iotDevice.setUserPlant(null);
            userPlant.setIotDevice(null);
        }

        userPlantRepository.delete(userPlant);
    }

    // 반려식물 수정용 정보 조회
    @Transactional
    public UserPlantEditDto getUserPlantForEdit(Long userPlantId, Long userId) {
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new IllegalArgumentException("반려식물을 찾을 수 없습니다."));

        if (!userPlant.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 반려식물만 조회할 수 있습니다.");
        }

        return new UserPlantEditDto(
                userPlant.getNickname(),
                userPlant.getAdoptedAt(),
                userPlant.isAutoControlEnabled(),
                userPlant.getPersonality().getId()
        );
    }

    // 반려식물 수정
    @Transactional
    public void editUserPlant(Long userPlantId, Long userId, UserPlantEditDto dto){
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new IllegalArgumentException("반려식물을 찾을 수 없습니다."));
        // 본인 소유의 반려식물이 아닌 경우
        if(!userPlant.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("본인의 반려식물만 수정할 수 있습니다.");
        }

        // 필드 수정
        if (dto.getNickname() != null) userPlant.setNickname(dto.getNickname());
        if (dto.getAdoptedAt() != null) userPlant.setAdoptedAt(dto.getAdoptedAt());
        if (dto.getAutoControlEnabled() != null) userPlant.setAutoControlEnabled(dto.getAutoControlEnabled());

        Personality newPersonality = null;
        if (dto.getPersonalityId() != null) {
            newPersonality = personalityRepository.findById(dto.getPersonalityId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 ID의 성격이 존재하지 않습니다."));
            userPlant.setPersonality(newPersonality);
        }
    }

}
