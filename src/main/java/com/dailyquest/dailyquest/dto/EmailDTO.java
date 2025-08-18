package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDTO {
    @NotBlank
    @Email(message = "이메일은 필수입니다.")
    private String email;
}
