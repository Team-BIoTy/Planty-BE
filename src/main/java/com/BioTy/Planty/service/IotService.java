package com.BioTy.Planty.service;

import com.BioTy.Planty.config.AdafruitClient;
import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.entity.UserPlant;
import com.BioTy.Planty.repository.IotRepository;
import com.BioTy.Planty.repository.UserPlantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IotService {
    private final IotRepository iotRepository;
    private final UserPlantRepository userPlantRepository;
    private final AdafruitClient adafruitClient;

    public List<IotDevice> getDevicesByUserId(Long userId){
        return iotRepository.findAllByUserId(userId);
    }

    public void sendAction(Long userPlantId, Long userId, String actionType) {
        UserPlant userPlant = userPlantRepository.findById(userPlantId)
                .orElseThrow(() -> new RuntimeException("반려식물이 존재하지 않습니다."));

        if (!userPlant.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 식물에 대한 권한이 없습니다.");
        }

        switch (actionType.toUpperCase()) {
            case "WATER" -> adafruitClient.sendCommand("action.water");
            case "FAN"   -> adafruitClient.sendCommand("action.fan");
            case "LIGHT" -> adafruitClient.sendCommand("action.light");
            default      -> throw new IllegalArgumentException("지원하지 않는 명령입니다.");
        }
    }
}
