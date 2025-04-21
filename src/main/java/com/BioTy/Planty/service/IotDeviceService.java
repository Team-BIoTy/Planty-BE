package com.BioTy.Planty.service;

import com.BioTy.Planty.entity.IotDevice;
import com.BioTy.Planty.repository.IotDeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IotDeviceService {
    private final IotDeviceRepository iotDeviceRepository;

    public List<IotDevice> getDevicesByUserId(Long userId){
        return iotDeviceRepository.findAllByUserId(userId);
    }
}
