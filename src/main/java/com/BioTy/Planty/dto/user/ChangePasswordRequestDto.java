package com.BioTy.Planty.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordRequestDto {
    private String currentPassword;
    private String newPassword;
}
