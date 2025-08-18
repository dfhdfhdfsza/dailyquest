package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    //영문 대소문자 + 숫자만 허용/ 4~20자
    @Pattern(regexp="^[a-zA-Z0-9]{4,20}$")
    private String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    //영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
    private  String password;

    private  String fingerprint;
}
