package com.dailyquest.dailyquest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDTO {
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;
}
