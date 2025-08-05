package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinDTO {
    String username;

    @NotBlank(message = "아이디는 필수입니다")
    @Size(min=4,max = 12,message = "아이디는 최소 4자 이상이어야 합니다.")
    String loginId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min=8,max = 12,message = "비밀번호는 최소 8자 이상이어야 합니다.")
    String password;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    String email;
}
