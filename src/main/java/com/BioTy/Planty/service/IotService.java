package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.repository.IotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IotService {
    private final IotRepository iotRepository;

    public List<IotDevice> getDevicesByUserId(Long userId){
        return iotRepository.findAllByUserId(userId);
    }
}
