package com.dailyquest.dailyquest.repository;

import com.dailyquest.dailyquest.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    boolean existsByLoginId(String id);

    Optional<UserEntity> findByLoginId(String uid);

    Optional<UserEntity> findByUid(long uid);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByLoginIdAndEmail(String loginId, String email);

    boolean existsByEmail(String email);

    //-------------로그인 시도 제한-------------

    //해당 loginId의 실패 횟수와 마지막 실패 시각을 DB에 저장
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.failedAttempts = :failed, u.lastFailedAt = :ts where u.loginId = :loginId")
    int updateFailStats(@Param("loginId") String loginId, @Param("failed") int failed, @Param("ts") Instant ts);

    //잠금 해제 예정 시각을 설정(null로 해제).
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.lockedUntil = :until where u.loginId = :loginId")
    int updateLockedUntil(@Param("loginId") String loginId, @Param("until") Instant until);

    //로그인 성공 등 정상 시에 실패 카운터/시각 초기화.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update UserEntity u set u.failedAttempts = 0, u.lastFailedAt = null where u.loginId = :loginId")
    int resetFailStats(@Param("loginId") String loginId);

}
