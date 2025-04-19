package com.BioTy.Planty.service;

import com.BioTy.Planty.dto.userPlant.UserPlantCreateRequestDto;
import com.BioTy.Planty.dto.userPlant.UserPlantSummaryResponseDto;
import com.BioTy.Planty.entity.Personality;
import com.BioTy.Planty.entity.PlantInfo;
import com.BioTy.Planty.entity.User;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.PersonalityRepository;
import com.BioTy.Planty.repository.PlantInfoRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import com.BioTy.Planty.repository.UserRepository;
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

    public List<UserPlantSummaryResponseDto> getUserPlants(Long userId){
        return userPlantRepository.findSummaryDtoByUserId(userId);
    }

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
}
