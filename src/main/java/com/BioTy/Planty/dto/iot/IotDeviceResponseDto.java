package com.BioTy.Planty.dto.iot;

import com.BioTy.Planty.entity.IotDevice;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IotDeviceResponseDto {
    private Long id;
    private String deviceSerial;
    private String model;
    private String status;

    public static IotDeviceResponseDto from(IotDevice device){
        return IotDeviceResponseDto.builder()
                .id(device.getId())
                .deviceSerial(device.getDeviceSerial())
                .model(device.getModel())
                .status(device.getStatus())
                .build();
    }
}
