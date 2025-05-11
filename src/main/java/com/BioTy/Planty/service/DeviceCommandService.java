package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.UserPlant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceCommandService {
    private final IotService iotService;

    public void sendActions(UserPlant userPlant, List<String> actionTypes) {
        for (String action : actionTypes) {
            try {
                iotService.sendAction(userPlant.getId(), userPlant.getUser().getId(), action);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
