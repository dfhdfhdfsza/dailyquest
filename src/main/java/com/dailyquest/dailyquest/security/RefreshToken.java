package com.dailyquest.dailyquest.security;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="refresh_tokens")
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String loginId;
    private String tokenHash;   // 토큰 원문 대신 해시 저장(bcrypt/sha256+salt)
    private String fingerprint;    //클라이언트 지문
    private Instant expiresAt;
    private boolean revoked=false;
}
