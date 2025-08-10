package com.dailyquest.dailyquest.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDTO {
    private String loginId;
    private  String email;
}
