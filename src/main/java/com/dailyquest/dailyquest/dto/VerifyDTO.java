package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDTO {

    @NotBlank(message = "아이디는 필수입니다.")
    //영문 대소문자 + 숫자만 허용/ 4~20자
    @Pattern(regexp="^[a-zA-Z0-9]{4,20}$")
    private String loginId;

    @NotBlank
    @Email(message = "이메일은 필수입니다.")
    private  String email;
}
