package com.dailyquest.dailyquest.security;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="refresh_tokens",indexes = {
        @Index(name="idx_tokenHash",columnList = "tokenHash"),
        @Index(name = "idx_expiresAt",columnList = "expiresAt"),
        @Index(name="idx_loginId_fingerprint",columnList = "loginId,fingerprint")
})
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "login_id")
    private String loginId;
    private String tokenHash;   // 토큰 원문 대신 해시 저장(bcrypt/sha256+salt)
    private String fingerprint;    //클라이언트 지문
    private Instant expiresAt;
    private Boolean persistent;
    private boolean revoked;
}
