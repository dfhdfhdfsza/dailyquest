package com.dailyquest.dailyquest.security;

import com.dailyquest.dailyquest.security.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    //토큰의 해시값이 일치하고 아직 취소(revoked = false)되지 않은 유효한 리프레시 토큰을 찾음
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);

    //특정 사용자의 모든 Refresh Token을 한 번에 무효화
    //로그아웃 시 이 메서드를 호출하면 해당 유저의 모든 기기에서 로그아웃 효과
    @Modifying  //update / delete는 데이터 변경 쿼리이므로 쓰기 작업임을 명시해야 함
    @Query("update RefreshToken r set r.revoked = true where r.loginId = :id")
    void revokeAllByUser(@Param("id") Long loginId);

    //요청한 유저의 현재 브라우저(디바이스)만 로그아웃
    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.tokenHash = :hash")
    void revokeByTokenHash(String hash);
}
