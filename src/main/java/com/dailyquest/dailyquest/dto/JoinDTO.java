package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinDTO {

    @NotBlank(message = "닉네임은 필수입니다.")
    String username;

    @NotBlank(message = "아이디는 필수입니다.")
    //영문 대소문자 + 숫자만 허용/ 4~20자
    @Pattern(regexp="^[a-zA-Z0-9]{4,20}$")
    String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    //영문 최소 1개 이상/숫자 최소 1개 이상/특수문자 최소 1개 이상/8~20자 제한
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
    String password;

    @NotBlank
    @Email(message = "이메일은 필수입니다.")
    String email;
}
