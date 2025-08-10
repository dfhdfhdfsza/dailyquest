package com.dailyquest.dailyquest.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetDTO {
    private String token;
    private String newPassword;
    private  String newPasswordConfirm;
}
