package com.BioTy.Planty.dto.fcm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendNotificationRequestDto {
    private String targetToken;
    private String title;
    private String body;
}
