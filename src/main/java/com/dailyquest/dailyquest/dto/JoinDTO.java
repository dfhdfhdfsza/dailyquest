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

    @NotBlank
    String username;

    @NotBlank
    @Pattern(regexp="^[A-Za-z][A-Za-z0-9_-]{8,19}$")
    String loginId;

    @NotBlank
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$")
    String password;

    @NotBlank @Email
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!\"#$%&'()*+,\\-./:;<=>?@\\[\\]\\\\^_`{|}~]{8,20}$")
    String email;
}
