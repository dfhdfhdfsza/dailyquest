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
    @Pattern(regexp="^[A-Za-z][A-Za-z0-9_-]{8,19}$")
    String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$")
    String password;

    @NotBlank @Email(message = "이메일은 필수입니다.")
    @Pattern(regexp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    String email;
}
