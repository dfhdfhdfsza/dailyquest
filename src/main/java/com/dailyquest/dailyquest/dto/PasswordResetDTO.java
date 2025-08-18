package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetDTO {

    private String token;

    //영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
    private String newPassword;

    //영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
    private  String newPasswordConfirm;
}
